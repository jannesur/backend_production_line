package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.vw.productionline.productionline.exceptions.ProductionLineAlreadyRunningException;
import de.vw.productionline.productionline.exceptions.ProductionLineIncompleteException;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineService;
import de.vw.productionline.productionline.productionline.SimulationStatus;
import de.vw.productionline.productionline.productionline.Status;
import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import de.vw.productionline.productionline.productionstep.RecoveryRunnable;
import de.vw.productionline.productionline.robot.MaintenanceRunnable;
import de.vw.productionline.productionline.robot.MaintenanceWaitingRunnable;
import de.vw.productionline.productionline.robot.Robot;

public class ProductionRunnable implements Runnable {
    private Production production;
    private ProductionLine productionLine;
    private String threadName;
    private long threadParentNumber;
    private long threadCount = 0l;
    private Logger logger = LoggerFactory.getLogger(ProductionRunnable.class);

    @Autowired
    private ProductionTimeService productionTimeService;

    @Autowired
    private ProductionService productionService;

    @Autowired
    private ProductionLineService productionLineService;

    public ProductionRunnable(Production production, String threadName, long threadParentNumber) {
        this.production = production;
        this.productionLine = production.getProductionLine();
        this.threadName = threadName;
        this.threadParentNumber = threadParentNumber;
    }

    @Override
    public void run() {

        logger.info(String.format("Start %s", threadName));

        if (!this.productionLine.getStatus().equals(Status.READY)) {
            synchronized (this) {
                logger.info(String.format("%s: production line %s not ready", this.threadName,
                        this.productionLine.getName()));
                throw new ProductionLineIncompleteException();
            }
        }

        if (this.productionLine.getSimulationStatus().equals(SimulationStatus.RUNNING)) {
            synchronized (this) {
                logger.info(String.format("%s: production line %s already running", this.threadName,
                        this.productionLine.getName()));
                throw new ProductionLineAlreadyRunningException();
            }
        }

        synchronized (this) {
            this.productionLine.setSimulationStatus(SimulationStatus.RUNNING);
            production.setStartTime(LocalDateTime.now());
            logger.info(String.format("%s: set status of production line %s to RUNNING", this.threadName,
                    this.productionLine.getName()));
        }

        while (!Thread.interrupted()) {
            logger.info(String.format("%s: starting a new car", this.threadName));

            for (ProductionStep productionStep : this.productionLine.getProductionSteps()) {
                synchronized (this) {
                    production.setCurrentProductionStep(productionStep);
                    logger.info(
                            String.format("%s: current production step is %s", this.threadName, productionStep));
                }

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance();
                startProduction(productionStep);
                startRecovery(productionStep, false);
            }

            if (!Thread.interrupted()) {
                synchronized (this) {
                    production.incrementProducedCars();
                    logger.info(String.format("%s: production line %s finished one car (total: %d)", this.threadName,
                            this.productionLine.getName(), production.getNumberProducedCars()));
                }
            }
        }

        // THREAD WAS INTERRUPTED
        logger.info(String.format("%s: production line %s is stopped", this.threadName,
                this.productionLine.getName()));
        cleanUp();
    }

    private void cleanUp() {
        logger.info(String.format("%s: clean up production line %s", this.threadName,
                this.production.getProductionLine().getName()));

        this.production.setEndTime(LocalDateTime.now());
        this.productionService.saveProduction(this.production);

        this.productionLine.setSimulationStatus(SimulationStatus.STOPPED);
        this.productionLine.setAllProductionStepStatus(ProductionStatus.WAITING);
        this.productionLine.resetAllProductionStepRecoveryTimes();
        this.productionLineService.updateProductionLine(productionLine);

        // TODO -> do I need to separately save any other objects to successfully shut
        // down the line?
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info(String.format("%s: check for failure in production step %s", this.threadName,
                productionStep.getName()));
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        if (Thread.interrupted()) {
            return;
        }

        logger.info(String.format("%s: deal with failure in production step %s", this.threadName,
                productionStep.getName()));
        if (isFailureStep(productionStep)) {
            logger.info(String.format("%s: there was a failure in production step %s", this.threadName,
                    productionStep.getName()));
            startRecovery(productionStep, true);
        }
        waitForRecovery(productionStep);
    }

    private void startProduction(ProductionStep productionStep) {
        if (Thread.interrupted()) {
            return;
        }

        long remainingProductionTime = productionStep.getDurationInMinutes();

        if (this.production.getNumberProducedCars() == 0 && productionStep instanceof Robot) {
            logger.info(
                    String.format("%s: first time robot %s in production -- start maintenance cycle", this.threadName,
                            productionStep.getName()));
            startMaintenanceCountdown((Robot) productionStep);
        }

        while (!Thread.interrupted() && remainingProductionTime > 0) {
            logger.info(String.format("%s: production step %s in production", this.threadName,
                    productionStep.getName()));
            try {
                synchronized (this) {
                    remainingProductionTime--;
                    logger.info(String.format("%s: remaining production time %d for production step %s",
                            this.threadName, remainingProductionTime, productionStep.getName()));
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info(String.format("%s: production step %s was interrupted during production",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }

        ProductionTime productionTime = new ProductionTime(ProductionTimeType.PRODUCTION,
                productionStep.getDurationInMinutes(), this.production);
        this.productionTimeService.saveProductionTime(productionTime);

    }

    private void startRecovery(ProductionStep productionStep, boolean isFailureRecovery) {
        if (Thread.interrupted()) {
            return;
        }

        this.threadCount++;
        String recoveryThreadName = String.format("%s subthread %d.%d - recovery",
                this.threadName, this.threadParentNumber, this.threadCount);
        Thread recoveryThread = new Thread(
                new RecoveryRunnable(productionStep, isFailureRecovery, recoveryThreadName, this.production,
                        this.productionTimeService),
                recoveryThreadName);
        synchronized (this) {
            logger.info(String.format("%s: start recovery for production step %s", this.threadName,
                    productionStep.getName()));
            recoveryThread.start();
        }
    }

    private void waitForRecovery(ProductionStep productionStep) {
        if (Thread.interrupted()) {
            return;
        }

        logger.info(String.format("%s: check if recovery time is needed for production step %s",
                this.threadName,
                productionStep.getName()));

        while (!Thread.interrupted() && productionStep.getProductionStatus().equals(ProductionStatus.RECOVERY)) {
            try {
                Thread.sleep(productionStep.getRemainingRecoveryTime() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("%s: production step %s was interrupted while waiting for recovery",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startMaintenanceCountdown(Robot robot) {
        this.threadCount++;
        String waitMaintThreadName = String.format("%s subthread %d.%d - maintenance waiting", this.threadName,
                this.threadParentNumber,
                this.threadCount);
        Thread waitForMaintenanceThread = new Thread(new MaintenanceWaitingRunnable(robot, waitMaintThreadName),
                waitMaintThreadName);
        waitForMaintenanceThread.start();
    }

    private void startMaintenance(List<Robot> robots) {
        for (Robot robot : robots) {
            this.threadCount++;
            String maintThreadName = String.format("%s subthread %d.%d - maintenance", this.threadName,
                    this.threadParentNumber,
                    this.threadCount);
            Thread performMaintenanceThread = new Thread(
                    new MaintenanceRunnable(robot, this.production, maintThreadName, this.productionTimeService),
                    maintThreadName);
            performMaintenanceThread.start();
        }
    }

    private void waitForMaintenance() {
        if (Thread.interrupted()) {
            return;
        }

        logger.info(String.format("%s: check if maintenance time is needed for production line %s",
                this.threadName,
                this.productionLine.getName()));

        while (!Thread.interrupted() && this.productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {

            logger.info(String.format("%s: maintenance time is needed for production line %s", this.threadName,
                    this.productionLine.getName()));

            List<Robot> robotsNeedMaintenance = this.productionLine.getProductionSteps().stream()
                    .filter(Robot.class::isInstance).map(Robot.class::cast)
                    .filter(robot -> robot.getProductionStatus().equals(ProductionStatus.NEEDS_MAINTENANCE)).toList();

            startMaintenance(robotsNeedMaintenance);

            try {
                Thread.sleep(this.productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("s%s: production line %s was interrupted while waiting for recovery",
                        this.threadName,
                        this.productionLine.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

}
