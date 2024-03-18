package de.vw.productionline.productionline.production;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

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

        // if thread is stopped/interrupted:
        // Set productionLine status to RUNNING
        // Set productionStep status to ...?

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
            for (ProductionStep productionStep : productionLine.getProductionSteps()) {
                synchronized (this) {
                    production.setCurrentProductionStep(productionStep);
                    logger.info("Thread %s: current productionstep is %s", productionStep);
                }
                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance(productionLine);

                long remainingProductionTime = productionStep.getDurationInMinutes();
                while (remainingProductionTime > 0) {
                    logger.info("Thread %s: is producing in productionstep %s", this.threadName, productionStep.getName());
                    try {
                        synchronized (this) {
                            remainingProductionTime--;
                            logger.info("Thread %s: remaining production time %d for productionstep %s", this.threadName,remainingProductionTime, productionStep.getName());
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        // TODO: implement line stopping logic
                    }
                }

                startRecovery(productionStep);
            }
        }
        synchronized (this) {
            production.incrementProducedCars();
            logger.info("Thread %s: productionline %s: produced one more car %d", this.threadName, productionLine.getName(), production.getNumberProducedCars());
        }
        // THREAD WAS INTERRUPTED
        synchronized (this) {
            logger.info("Thread %s: productionline %s is stopped", this.threadName, productionLine.getName());
            production.setEndTime(LocalDateTime.now());
        }
        cleanUp();
    }

    private void cleanUp() {
        // TODO -> save everything so that the production line can be stopped
        logger.info("Thread %s: cleaning up productionline %s", this.threadName, this.production.getProductionLine().getName());
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info("Thread %s: is checking for failure in productionstep %s", this.threadName, productionStep.getName());
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        logger.info("Thread %s: is dealing with failure in productionstep %s", this.threadName, productionStep.getName());
        if (isFailureStep(productionStep)) {
            logger.info("Thread %s: there was a failure in productionstep %s", this.threadName, productionStep.getName());
            startRecovery(productionStep);
        }
        waitForRecovery(productionStep);
    }

    private void startRecovery(ProductionStep productionStep) {
        // TODO -> is it a bad idea to keep creating "throw-away" threads?
        Thread recoveryThread = new Thread(new RecoveryRunnable(productionStep));
        synchronized (this) {
            logger.info("Thread %s: starts recovery for productionstep %s", this.threadName, productionStep.getName());
            recoveryThread.start();
        }
    }

    private void waitForRecovery(ProductionStep productionStep) {
        // need to make sure that
        logger.info("Thread %s: is checking if recovery time is needed for productionstep %s", this.threadName, productionStep.getName());
        while (productionStep.getProductionStatus().equals(ProductionStatus.RECOVERY)) {
            logger.info("Thread %s: recovery time is needed for productionstep %s", this.threadName, productionStep.getName());
            try {
                Thread.sleep(productionStep.getRemainingRecoveryTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: implement line stopping logic
            }
        }
    }

    private void waitForMaintenance(ProductionLine productionLine) {
        logger.info("Thread %s: is checking if maintenance time is needed for productionline %s", this.threadName, productionLine.getName());
        while (productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {
            logger.info("Thread %s: maintenance time is needed for productionline %s", this.threadName, productionLine.getName());
            try {
                Thread.sleep(productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: implement line stopping logic
            }

        }
    }

}
