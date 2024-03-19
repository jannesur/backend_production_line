package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.exceptions.ProductionLineAlreadyRunningException;
import de.vw.productionline.productionline.exceptions.ProductionLineIncompleteException;
import de.vw.productionline.productionline.productionline.ProductionLine;
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
    private ProductionService productionService;
    private long threadCount = 0l;
    private boolean interrupted = false;
    private Logger logger = LoggerFactory.getLogger(ProductionRunnable.class);

    public ProductionRunnable(Production production, String threadName, long threadParentNumber,
            ProductionService productionService) {
        this.production = production;
        this.productionLine = production.getProductionLine();
        this.threadName = threadName;
        this.threadParentNumber = threadParentNumber;
        this.productionService = productionService;
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

        if (Thread.interrupted()) {
            this.interrupted = true;
        }

        while (!this.interrupted) {
            logger.info(String.format("%s: starting a new car", this.threadName));

            for (ProductionStep productionStep : this.productionLine.getProductionSteps()) {
                synchronized (this) {
                    production.setCurrentProductionStep(productionStep);
                    logger.info(
                            String.format("%s: current production step is %s", this.threadName, productionStep));
                }

                if (!this.interrupted) {
                    waitForRecovery(productionStep);
                }

                if (!this.interrupted) {
                    dealWithFailure(productionStep);
                }

                if (!this.interrupted) {
                    waitForMaintenance();
                }

                if (!this.interrupted) {
                    startProduction(productionStep);
                }

                if (!this.interrupted) {
                    startRecovery(productionStep, false);
                }

            }

            if (Thread.interrupted()) {
                this.interrupted = true;
            }

            if (!this.interrupted) {
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
        this.productionLine.setSimulationStatus(SimulationStatus.STOPPED);
        this.productionLine.setAllProductionStepStatus(ProductionStatus.WAITING);
        this.productionLine.resetAllProductionStepRecoveryTimes();
        this.productionService.saveProduction(this.production);
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info(String.format("%s: check for failure in production step %s", this.threadName,
                productionStep.getName()));
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            this.interrupted = true;
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
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted in production", this.threadName));
            this.interrupted = true;
            return;
        }

        if (this.production.getNumberProducedCars() == 0 && productionStep instanceof Robot) {
            logger.info(
                    String.format("%s: first time robot %s in production -- start maintenance cycle", this.threadName,
                            productionStep.getName()));
            startMaintenanceCountdown((Robot) productionStep);
        }

        long remainingProductionTime = productionStep.getDurationInMinutes();

        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
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

        logger.info(String.format("%s: saving production time for step %s",
                this.threadName,
                productionStep.getName()));
        ProductionTime productionTime = new ProductionTime(ProductionTimeType.PRODUCTION,
                productionStep.getDurationInMinutes(), this.production);
        this.production.addProductionTime(productionTime);

    }

    private void startRecovery(ProductionStep productionStep, boolean isFailureRecovery) {
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted while starting recovery", this.threadName));
            return;
        }

        this.threadCount++;
        String recoveryThreadName = String.format("%s subthread %d.%d - recovery",
                this.threadName, this.threadParentNumber, this.threadCount);
        Thread recoveryThread = new Thread(
                new RecoveryRunnable(productionStep, isFailureRecovery, recoveryThreadName, this.production),
                recoveryThreadName);
        synchronized (this) {
            logger.info(String.format("%s: start recovery for production step %s", this.threadName,
                    productionStep.getName()));
            recoveryThread.start();
        }
    }

    private void waitForRecovery(ProductionStep productionStep) {
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted while waiting for recovery", this.threadName));
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
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted while waiting for recovery", this.threadName));
            return;
        }

        this.threadCount++;
        String waitMaintThreadName = String.format("%s subthread %d.%d - maintenance waiting", this.threadName,
                this.threadParentNumber,
                this.threadCount);
        Thread waitForMaintenanceThread = new Thread(new MaintenanceWaitingRunnable(robot, waitMaintThreadName),
                waitMaintThreadName);
        waitForMaintenanceThread.start();
    }

    private void startMaintenance(List<Robot> robots) {
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted while waiting for recovery", this.threadName));
            return;
        }

        for (Robot robot : robots) {
            this.threadCount++;
            String maintThreadName = String.format("%s subthread %d.%d - maintenance", this.threadName,
                    this.threadParentNumber,
                    this.threadCount);
            Thread performMaintenanceThread = new Thread(
                    new MaintenanceRunnable(robot, this.production, maintThreadName),
                    maintThreadName);
            performMaintenanceThread.start();
        }
    }

    private void waitForMaintenance() {
        logger.info(String.format("%s Thread.interrupted() is %b", this.threadName, Thread.interrupted()));
        if (Thread.interrupted()) {
            logger.info(String.format("%s interrupted waiting for maintenance", this.threadName));
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
                logger.info(String.format("s%s: production line %s was interrupted while waiting for maintenance",
                        this.threadName,
                        this.productionLine.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

}
