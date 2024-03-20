package de.vw.productionline.productionline.robot;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productiontime.ProductionTime;
import de.vw.productionline.productionline.productiontime.ProductionTimeType;

public class MaintenanceRunnable implements Runnable {

    private Robot robot;
    private String threadName;
    private Consumer<ProductionTime> productionTimeConsumer;

    private Logger logger = LoggerFactory.getLogger(MaintenanceRunnable.class);

    public MaintenanceRunnable(Robot robot, String threadName,
            Consumer<ProductionTime> productionTimeConsumer) {
        this.robot = robot;
        this.threadName = threadName;
        this.productionTimeConsumer = productionTimeConsumer;
    }

    @Override
    public void run() {
        logger.info(
                String.format("%s: starting maintenance for robot %s", this.threadName, this.robot.getName()));
        long maintenanceTimeLeft = this.robot.getMaintenanceTimeInMinutes();
        while (maintenanceTimeLeft > 0) {
            maintenanceTimeLeft--;
            logger.info(
                    String.format("%s: %d maintenance time left for robot %s", this.threadName, maintenanceTimeLeft,
                            this.robot.getName()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.info(String.format("%s: interrupted while performing maintenance on robot %s",
                        this.threadName,
                        this.robot.getName()));
                Thread.currentThread().interrupt();
                return;
            }
        }

        this.robot.setProductionStatus(ProductionStatus.WAITING);

        logger.info(String.format("%s: saving maintenance time for robot %s",
                this.threadName,
                robot.getName()));
        ProductionTime productionTime = new ProductionTime(ProductionTimeType.MAINTENANCE,
                this.robot.getMaintenanceTimeInMinutes(), null);
        this.productionTimeConsumer.accept(productionTime);
    }

}
