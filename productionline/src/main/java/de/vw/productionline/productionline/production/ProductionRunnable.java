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
            logger.info(String.format("Thread %s: set status of production line %s to RUNNING", this.threadName,
                    productionLine.getName()));
        }

        while (!Thread.interrupted()) {
            for (ProductionStep productionStep : productionLine.getProductionSteps()) {
                production.setCurrentProductionStep(productionStep);

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance(productionLine);

                long remainingProductionTime = productionStep.getDurationInMinutes();
                while (remainingProductionTime > 0) {
                    try {
                        remainingProductionTime--;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        // TODO: implement line stopping logic
                    }
                }

                startRecovery(productionStep);
            }
        }

        // THREAD WAS INTERRUPTED

    }

    private void cleanUp() {
        // TODO -> save everything so that the production line can be stopped
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        return Math.random() < productionStep.getFailureProbability();
    }

    private void dealWithFailure(ProductionStep productionStep) {
        if (isFailureStep(productionStep)) {
            startRecovery(productionStep);
        }
        waitForRecovery(productionStep);
    }

    private void startRecovery(ProductionStep productionStep) {
        // TODO -> is it a bad idea to keep creating "throw-away" threads?
        Thread recoveryThread = new Thread(new RecoveryRunnable(productionStep));
        recoveryThread.start();
    }

    private void waitForRecovery(ProductionStep productionStep) {
        // need to make sure that
        while (productionStep.getProductionStatus().equals(ProductionStatus.RECOVERY)) {
            try {
                Thread.sleep(productionStep.getRemainingRecoveryTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: implement line stopping logic
            }
        }
    }

    private void waitForMaintenance(ProductionLine productionLine) {
        while (productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {
            try {
                Thread.sleep(productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: implement line stopping logic
            }

        }
    }

}
