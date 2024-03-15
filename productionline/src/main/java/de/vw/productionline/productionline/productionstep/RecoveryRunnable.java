package de.vw.productionline.productionline.productionstep;

public class RecoveryRunnable implements Runnable {

    private ProductionStep productionStep;

    public RecoveryRunnable(ProductionStep productionStep) {
        this.productionStep = productionStep;
    }

    @Override
    public void run() {
        this.productionStep.setProductionStatus(ProductionStatus.RECOVERY);
        while (this.productionStep.getRemainingRecoveryTime() > 0) {
            try {
                Thread.sleep(1000);
                this.productionStep.reduceRecoveryTimeByOneMinute();
            } catch (InterruptedException ex) {
                // TODO deal with stop in production line
                ex.printStackTrace();
            }
        }
        this.productionStep.setProductionStatus(ProductionStatus.WAITING);
    }

}
