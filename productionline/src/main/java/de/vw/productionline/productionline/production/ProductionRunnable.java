package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;

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

public class ProductionRunnable implements Runnable {
    private Production production;
    private Logger logger = LoggerFactory.getLogger(ProductionRunnable.class);
    private String threadName = Thread.currentThread().getName();

    public ProductionRunnable(Production production) {
        this.production = production;
    }

    @Override
    public void run() {
        // TODO: use pre-destroy annotation to ensure that data is saved to database
        // TODO if TIME! every 5 seconds save Production to the database (in case the
        // server fails)

        logger.info(String.format("Started thread %s", threadName));

        ProductionLine productionLine = production.getProductionLine();

        if (!productionLine.getStatus().equals(Status.READY)) {
            synchronized (this) {
                logger.info(String.format("Thread %s: production line %s not ready", this.threadName,
                        productionLine.getName()));
                throw new ProductionLineIncompleteException();
            }
        }

        if (productionLine.getSimulationStatus().equals(SimulationStatus.RUNNING)) {
            synchronized (this) {
                logger.info(String.format("Thread %s: production line %s already running", this.threadName,
                        productionLine.getName()));
                throw new ProductionLineAlreadyRunningException();
            }
        }

        synchronized (this) {
            productionLine.setSimulationStatus(SimulationStatus.RUNNING);
            production.setStartTime(LocalDateTime.now());
            logger.info(String.format("Thread %s: set status of production line %s to RUNNING", this.threadName,
                    productionLine.getName()));
        }

        while (!Thread.interrupted()) {
            logger.info(String.format("Thread %s: inside the while loop checking for interruptions", this.threadName));

            for (ProductionStep productionStep : productionLine.getProductionSteps()) {
                logger.info(String.format("Thread %s: looping through production steps", this.threadName));
                synchronized (this) {
                    production.setCurrentProductionStep(productionStep);
                    logger.info(
                            String.format("Thread %s: current production step is %s", this.threadName, productionStep));
                }

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance(productionLine);
                startProduction(productionStep);
                startRecovery(productionStep);
            }

            synchronized (this) {
                production.incrementProducedCars();
                logger.info(String.format("Thread %s: production line %s: produced one more car %d", this.threadName,
                        productionLine.getName(), production.getNumberProducedCars()));
            }
        }

        // THREAD WAS INTERRUPTED
        logger.info(String.format("Thread %s: production line %s is stopped", this.threadName,
                productionLine.getName()));
        cleanUp();
    }

    private void cleanUp() {
        // TODO -> save everything so that the production line can be stopped
        // update statuses
        this.production.setEndTime(LocalDateTime.now());
        logger.info(String.format("Thread %s: cleaning up production line %s", this.threadName,
                this.production.getProductionLine().getName()));
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info(String.format("Thread %s: is checking for failure in production step %s", this.threadName,
                productionStep.getName()));
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        logger.info(String.format("Thread %s: is dealing with failure in productionstep %s", this.threadName,
                productionStep.getName()));
        if (isFailureStep(productionStep)) {
            logger.info(String.format("Thread %s: there was a failure in productionstep %s", this.threadName,
                    productionStep.getName()));
            startRecovery(productionStep);
        }
        waitForRecovery(productionStep);
    }

    private void startProduction(ProductionStep productionStep) {
        long remainingProductionTime = productionStep.getDurationInMinutes();
        while (!Thread.interrupted() && remainingProductionTime > 0) {
            logger.info(String.format("Thread %s: is producing in production step %s", this.threadName,
                    productionStep.getName()));
            try {
                synchronized (this) {
                    remainingProductionTime--;
                    logger.info(String.format("Thread %s: remaining production time %d for production step %s",
                            this.threadName, remainingProductionTime, productionStep.getName()));
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info(String.format("Thread %s: production step %s was interrupted during production",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startRecovery(ProductionStep productionStep) {

        Thread recoveryThread = new Thread(new RecoveryRunnable(productionStep));
        synchronized (this) {
            logger.info(String.format("Thread %s: starts recovery for production step %s", this.threadName,
                    productionStep.getName()));
            recoveryThread.start();
        }
    }

    private void waitForRecovery(ProductionStep productionStep) {
        // need to make sure that
        logger.info(String.format("Thread %s: is checking if recovery time is needed for production step %s",
                this.threadName,
                productionStep.getName()));
        while (!Thread.interrupted() && productionStep.getProductionStatus().equals(ProductionStatus.RECOVERY)) {
            try {
                Thread.sleep(productionStep.getRemainingRecoveryTime() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("Thread %s: production step %s was interrupted while waiting for recovery",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void waitForMaintenance(ProductionLine productionLine) {
        logger.info(String.format("Thread %s: is checking if maintenance time is needed for production line %s",
                this.threadName,
                productionLine.getName()));
        while (!Thread.interrupted() && productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {
            logger.info(String.format("Thread %s: maintenance time is needed for production line %s", this.threadName,
                    productionLine.getName()));
            try {
                Thread.sleep(productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("Thread %s: production line %s was interrupted while waiting for recovery",
                        this.threadName,
                        productionLine.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

}
