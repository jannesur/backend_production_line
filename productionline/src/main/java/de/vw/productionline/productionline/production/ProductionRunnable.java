package de.vw.productionline.productionline.production;

import de.vw.productionline.productionline.productionline.ProductionLine;

public class ProductionRunnable implements Runnable {
    private Production production;
    private ProductionService productionService;

    @Override
    public void run() {
        // Is production in status READY? Only then go through next steps
        // Set productionLine status to RUNNING

        // loop through the production steps
        // Set step to current ProductionStep
        // loop through production steps and if recovery time != 0, reduce by one second

        // (as version 1: only check at the beginning of each next production step if
        // any robots need maintenance)
        // if more than 1 robot needs maintenance -> check which maintenance time is
        // longest and wait that amount of time

        // check if step is failure step
        // set status to RECOVERY
        // if yes -> wait one additional recovery time

        // every 5 seconds save Production to the database

        // if thread is stopped/interrupted:
        // Set productionLine status to RUNNING
        // Set productionStep status to ...?
    }

}
