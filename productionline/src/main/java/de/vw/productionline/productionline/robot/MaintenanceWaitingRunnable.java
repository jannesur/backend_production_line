package de.vw.productionline.productionline.robot;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.productionstep.ProductionStatus;

public class MaintenanceWaitingRunnable implements Runnable {
    private Robot robot;
    private String threadName;
    private Consumer<String> endThreadConsumer;
    private Logger logger = LoggerFactory.getLogger(MaintenanceWaitingRunnable.class);

    public MaintenanceWaitingRunnable(Robot robot, String threadName, Consumer<String> endThreadConsumer) {
        this.robot = robot;
        this.threadName = threadName;
        this.endThreadConsumer = endThreadConsumer;
    }

    @Override
    public void run() {
        logger.info(String.format("%s: counting down to next maintenance for robot %s", this.threadName,
                this.robot.getName()));
        long timeToMaintenance = this.robot.getMaintenanceCycleInMinutes();
        logger.info(String.format("%s: robot %s has %d minutes to wait", this.threadName,
                this.robot.getName(), timeToMaintenance));
        while (!Thread.currentThread().isInterrupted() && timeToMaintenance > 0) {
            timeToMaintenance--;
            // logger.info(String.format("%s: %d minutes left to wait for maintenance for
            // robot %s", this.threadName,
            // timeToMaintenance, this.robot.getName()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.info(String.format("%s: interrupted while counting down maintenance for robot %s",
                        this.threadName, this.robot.getName()));
                Thread.currentThread().interrupt();
                return;
            }
        }
        logger.info(String.format("%s: robot %s now needs maintenance", this.threadName,
                this.robot.getName()));
        this.robot.setProductionStatus(ProductionStatus.NEEDS_MAINTENANCE);
        this.endThreadConsumer.accept(this.threadName);
    }
}
