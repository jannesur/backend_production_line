package de.vw.productionline.productionline.productionstep;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.productiontime.ProductionTime;
import de.vw.productionline.productionline.productiontime.ProductionTimeType;

public class RecoveryRunnable implements Runnable {

    private ProductionStep productionStep;
    private boolean isFailureRecovery;
    private String threadName;
    private Consumer<ProductionTime> productionTimeConsumer;
    private Consumer<String> endThreadConsumer;

    private Logger logger = LoggerFactory.getLogger(RecoveryRunnable.class);

    public RecoveryRunnable(ProductionStep productionStep, boolean isFailureRecovery, String threadName,
            Consumer<ProductionTime> productionTimeConsumer, Consumer<String> endThreadConsumer) {
        this.productionStep = productionStep;
        this.isFailureRecovery = isFailureRecovery;
        this.threadName = threadName;
        this.productionTimeConsumer = productionTimeConsumer;
        this.endThreadConsumer = endThreadConsumer;
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

        while (!Thread.currentThread().isInterrupted() && this.productionStep.getRemainingRecoveryTime() > 0) {
            try {
                synchronized (this) {
                    long remainingTime = this.productionStep.reduceRecoveryTimeByOneMinute();
                    // logger.info(String.format("%s: production step %s recovery time left %d",
                    // this.threadName,
                    // this.productionStep.getName(), remainingTime));
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
        logger.info(String.format("%s: production step %s is now recovered", this.threadName,
                this.productionStep.getName()));

        if (!Thread.currentThread().isInterrupted() && this.isFailureRecovery) {
            synchronized (this) {
                logger.info(String.format("%s: saving failure time for step %s",
                        this.threadName,
                        productionStep.getName()));
                ProductionTime productionTime = new ProductionTime(ProductionTimeType.FAILURE,
                        productionStep.getTimeToRecovery(), null);
                this.productionTimeConsumer.accept(productionTime);
            }
        }

        this.endThreadConsumer.accept(this.threadName);
    }

}
