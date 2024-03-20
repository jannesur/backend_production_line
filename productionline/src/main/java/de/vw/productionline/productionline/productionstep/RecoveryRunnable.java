package de.vw.productionline.productionline.productionstep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.production.Production;
import de.vw.productionline.productionline.production.ProductionTime;
import de.vw.productionline.productionline.production.ProductionTimeType;

public class RecoveryRunnable implements Runnable {

    private ProductionStep productionStep;
    private boolean isFailureRecovery;
    private String threadName;
    private Production production;
    private Logger logger = LoggerFactory.getLogger(RecoveryRunnable.class);

    public RecoveryRunnable(ProductionStep productionStep, boolean isFailureRecovery, String threadName,
            Production production) {
        this.productionStep = productionStep;
        this.isFailureRecovery = isFailureRecovery;
        this.threadName = threadName;
        this.production = production;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.productionStep.setProductionStatus(ProductionStatus.RECOVERY);
            logger.info(String.format("%s: set production status of step %s to RECOVERY", this.threadName,
                    this.productionStep.getName()));
        }

        synchronized (this) {
            long recoveryTimeRemaining = productionStep.getTimeToRecovery();
            this.productionStep.setRemainingRecoveryTime(recoveryTimeRemaining);
            logger.info(String.format("%s: production step %s has %d recovery time left", this.threadName,
                    this.productionStep.getName(), recoveryTimeRemaining));
        }

        while (!Thread.interrupted() && this.productionStep.getRemainingRecoveryTime() > 0) {
            try {
                synchronized (this) {
                    long remainingTime = this.productionStep.reduceRecoveryTimeByOneMinute();
                    logger.info(String.format("%s: production step %s recovery time left %d", this.threadName,
                            this.productionStep.getName(), remainingTime));
                }

                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                logger.info(String.format("%s: production step %s was interrupted", this.threadName,
                        this.productionStep.getName()));
                Thread.currentThread().interrupt();
                return;
            }
        }
        this.productionStep.setProductionStatus(ProductionStatus.WAITING);

        if (this.isFailureRecovery) {
            logger.info(String.format("%s: saving failure time for step %s",
                    this.threadName,
                    productionStep.getName()));
            ProductionTime productionTime = new ProductionTime(ProductionTimeType.FAILURE,
                    productionStep.getTimeToRecovery(), this.production);
            this.production.addProductionTime(productionTime);
        }

    }

}
