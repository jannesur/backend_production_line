package de.vw.productionline.productionline.production;

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
    private ProductionService productionService;

    public ProductionRunnable(Production production, ProductionService productionService) {
        this.production = production;
        this.productionService = productionService;
    }

    @Override
    public void run() {

        // check if step is failure step
        // set status to RECOVERY
        // if yes -> wait one additional recovery time

        // every 5 seconds save Production to the database

        // if thread is stopped/interrupted:
        // Set productionLine status to RUNNING
        // Set productionStep status to ...?

        ProductionLine productionLine = production.getProductionLine();

        if (!productionLine.getStatus().equals(Status.READY)) {
            throw new ProductionLineIncompleteException();
        }
        if (productionLine.getSimulationStatus().equals(SimulationStatus.RUNNING)) {
            throw new ProductionLineAlreadyRunningException();
        }
        productionLine.setSimulationStatus(SimulationStatus.RUNNING);

        while (true) {
            for (ProductionStep productionStep : productionLine.getProductionSteps()) {
                production.setCurrentProductionStep(productionStep);

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance(productionLine);

                // do the stuff!

                // start recovery for machine
            }

        }

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
