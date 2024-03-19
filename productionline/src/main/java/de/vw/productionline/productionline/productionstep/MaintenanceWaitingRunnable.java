package de.vw.productionline.productionline.productionstep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.robot.Robot;

public class MaintenanceWaitingRunnable implements Runnable {
    private Robot robot;
    private String threadName;
    private Logger logger = LoggerFactory.getLogger(MaintenanceWaitingRunnable.class);

    public MaintenanceWaitingRunnable(Robot robot, String threadName) {
        this.robot = robot;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        logger.info(String.format("Thread %s: counting down to next maintenance for robot %s", this.threadName,
                this.robot.getName()));
        long timeToMaintenance = this.robot.getMaintenanceCycleInMinutes();
        while (timeToMaintenance > 0) {
            timeToMaintenance--;
            logger.info(String.format("Thread %s: %d minutes left to wait for maintenace for robot %s", this.threadName,
                    timeToMaintenance, this.robot.getName()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.info(String.format("Thread %s: interrupted while counting down maintenance for robot %s",
                        this.threadName, this.robot.getName()));
                Thread.currentThread().interrupt();
            }
        }

        this.robot.setProductionStatus(ProductionStatus.NEEDS_MAINTENANCE);
    }
}
