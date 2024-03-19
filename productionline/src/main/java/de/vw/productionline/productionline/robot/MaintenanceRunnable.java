package de.vw.productionline.productionline.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.productionstep.ProductionStatus;

public class MaintenanceRunnable implements Runnable {

    private Robot robot;
    private String threadName;
    private Logger logger = LoggerFactory.getLogger(MaintenanceRunnable.class);

    public MaintenanceRunnable(Robot robot, String threadName) {
        this.robot = robot;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        logger.info(
                String.format("Thread %s: starting maintenance for robot %s", this.threadName, this.robot.getName()));
        long maintenanceTimeLeft = this.robot.getMaintenanceTimeInMinutes();
        while (maintenanceTimeLeft > 0) {
            maintenanceTimeLeft--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.info(String.format("Thread %s: interrupted while performing maintenance on robot %s",
                        this.threadName,
                        this.robot.getName()));
                Thread.currentThread().interrupt();
            }
        }

        this.robot.setProductionStatus(ProductionStatus.WAITING);
    }

}
