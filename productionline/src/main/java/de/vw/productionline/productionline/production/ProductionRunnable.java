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
    private Logger logger = LoggerFactory.getLogger(ProductionRunnable.class);
    private String threadName;
    private long threadParentNumber;
    private long threadCount = 0l;

    public ProductionRunnable(Production production, String threadName, long threadParentNumber) {
        this.production = production;
        this.threadName = threadName;
        this.threadParentNumber = threadParentNumber;
    }

    @Override
    public void run() {

        logger.info(String.format("Start %s", threadName));

        ProductionLine productionLine = production.getProductionLine();

        if (!productionLine.getStatus().equals(Status.READY)) {
            synchronized (this) {
                logger.info(String.format("%s: production line %s not ready", this.threadName,
                        productionLine.getName()));
                throw new ProductionLineIncompleteException();
            }
        }

        if (productionLine.getSimulationStatus().equals(SimulationStatus.RUNNING)) {
            synchronized (this) {
                logger.info(String.format("%s: production line %s already running", this.threadName,
                        productionLine.getName()));
                throw new ProductionLineAlreadyRunningException();
            }
        }

        synchronized (this) {
            productionLine.setSimulationStatus(SimulationStatus.RUNNING);
            production.setStartTime(LocalDateTime.now());
            logger.info(String.format("%s: set status of production line %s to RUNNING", this.threadName,
                    productionLine.getName()));
        }

        while (!Thread.interrupted()) {
            logger.info(String.format("%s: inside the while loop checking for interruptions", this.threadName));

            for (ProductionStep productionStep : productionLine.getProductionSteps()) {
                logger.info(String.format("%s: loop through production steps", this.threadName));
                synchronized (this) {
                    production.setCurrentProductionStep(productionStep);
                    logger.info(
                            String.format("%s: current production step is %s", this.threadName, productionStep));
                }

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance(productionLine);
                startProduction(productionStep);
                startRecovery(productionStep);
            }

            synchronized (this) {
                production.incrementProducedCars();
                logger.info(String.format("%s: production line %s produced one car (total: %d)", this.threadName,
                        productionLine.getName(), production.getNumberProducedCars()));
            }
        }

        // THREAD WAS INTERRUPTED
        logger.info(String.format("%s: production line %s is stopped", this.threadName,
                productionLine.getName()));
        cleanUp();
    }

    private void cleanUp() {
        // TODO -> save everything so that the production line can be stopped
        // update statuses (all production steps need to go back to "waiting" and
        // production line needs to go to "stopped")

        logger.info(String.format("%s: clean up production line %s", this.threadName,
                this.production.getProductionLine().getName()));
        this.production.setEndTime(LocalDateTime.now());
        // START HERE
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info(String.format("%s: check for failure in production step %s", this.threadName,
                productionStep.getName()));
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        logger.info(String.format("%s: deal with failure in production step %s", this.threadName,
                productionStep.getName()));
        if (isFailureStep(productionStep)) {
            logger.info(String.format("%s: there was a failure in production step %s", this.threadName,
                    productionStep.getName()));
            startRecovery(productionStep);
        }
        waitForRecovery(productionStep);
    }

    private void startProduction(ProductionStep productionStep) {
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
    }

    private void startRecovery(ProductionStep productionStep) {
        this.threadCount++;
        String recoveryThreadName = String.format("%s subthread %d.%d - recovery",
                this.threadName, this.threadParentNumber, this.threadCount);
        Thread recoveryThread = new Thread(new RecoveryRunnable(productionStep, recoveryThreadName),
                recoveryThreadName);
        synchronized (this) {
            logger.info(String.format("%s: start recovery for production step %s", this.threadName,
                    productionStep.getName()));
            recoveryThread.start();
        }
    }

    private void waitForRecovery(ProductionStep productionStep) {
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
            Thread performMaintenanceThread = new Thread(new MaintenanceRunnable(robot, maintThreadName),
                    maintThreadName);
            performMaintenanceThread.start();
        }
    }

    private void waitForMaintenance(ProductionLine productionLine) {
        logger.info(String.format("%s: check if maintenance time is needed for production line %s",
                this.threadName,
                productionLine.getName()));
        while (!Thread.interrupted() && productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {
            logger.info(String.format("%s: maintenance time is needed for production line %s", this.threadName,
                    productionLine.getName()));
            List<Robot> robotsNeedMaintenance = productionLine.getProductionSteps().stream()
                    .filter(Robot.class::isInstance).map(Robot.class::cast)
                    .filter(robot -> robot.getProductionStatus().equals(ProductionStatus.NEEDS_MAINTENANCE)).toList();
            startMaintenance(robotsNeedMaintenance);
            try {
                Thread.sleep(productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("s%s: production line %s was interrupted while waiting for recovery",
                        this.threadName,
                        productionLine.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

}
