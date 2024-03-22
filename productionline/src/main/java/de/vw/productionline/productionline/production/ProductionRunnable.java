package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.SimulationStatus;
import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import de.vw.productionline.productionline.productionstep.RecoveryRunnable;
import de.vw.productionline.productionline.productiontime.ProductionTime;
import de.vw.productionline.productionline.productiontime.ProductionTimeType;
import de.vw.productionline.productionline.robot.MaintenanceRunnable;
import de.vw.productionline.productionline.robot.MaintenanceWaitingRunnable;
import de.vw.productionline.productionline.robot.Robot;

public class ProductionRunnable implements Runnable {

    // Set through constructor
    private final Production production;
    private final String threadName;
    private final long threadParentNumber;
    private final BiConsumer<Production, Set<ProductionTime>> saveProductionAndProductionTimesConsumer;

    // Other necessary instance variables
    private final ProductionLine productionLine;
    private Set<ProductionTime> productionTimes = new HashSet<>();
    private Map<String, Thread> childrenThreads = new HashMap<>();
    private long threadCount = 0l;
    private final Consumer<ProductionTime> productionTimeConsumer = productionTime -> productionTimes
            .add(productionTime);
    private final Consumer<Robot> robotMaintenanceConsumer = robot -> startMaintenanceCountdown(robot);
    private final Consumer<String> endThreadConsumer = threadName -> childrenThreads.remove(threadName);
    private final Logger logger = LoggerFactory.getLogger(ProductionRunnable.class);

    public ProductionRunnable(Production production, String threadName, long threadParentNumber,
            BiConsumer<Production, Set<ProductionTime>> saveProductionAndProductionTimes) {
        this.production = production;
        this.productionLine = production.getProductionLine();
        this.threadName = threadName;
        this.threadParentNumber = threadParentNumber;
        this.saveProductionAndProductionTimesConsumer = saveProductionAndProductionTimes;
    }

    @Override
    public void run() {

        logger.info(String.format("Start %s", threadName));
        this.production.setStartTime(LocalDateTime.now());

        List<ProductionStep> productionStepsInOrder = this.productionLine.getProductionSteps().stream()
                .sorted((step1, step2) -> Integer.compare(step1.getStep(), step2.getStep()))
                .toList();

        while (!Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s: starting a new car", this.threadName));

            for (ProductionStep productionStep : productionStepsInOrder) {
                logger.info(
                        String.format("%s: current production step is %s", this.threadName, productionStep));

                waitForRecovery(productionStep);
                dealWithFailure(productionStep);
                waitForMaintenance();
                startProduction(productionStep);
                startRecovery(productionStep, false);
            }

            if (!Thread.currentThread().isInterrupted()) {
                synchronized (this) {
                    production.incrementProducedCars();
                    logger.info(String.format("%s: production line %s finished one car (total: %d)", this.threadName,
                            this.productionLine.getName(), production.getNumberProducedCars()));
                }
            }
        }

        // THREAD WAS INTERRUPTED
        logger.info(String.format("%s: production line %s is stopped", this.threadName,
                this.productionLine.getName()));
        cleanUp();
    }

    private void waitForRecovery(ProductionStep productionStep) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted before production step %s could check for recovery",
                    this.threadName, productionStep.getName()));
            return;
        }

        logger.info(String.format("%s: check if recovery time is needed for production step %s",
                this.threadName,
                productionStep.getName()));

        while (!Thread.currentThread().isInterrupted()
                && productionStep.getProductionStatus().equals(ProductionStatus.RECOVERY)) {
            try {
                Thread.sleep(productionStep.getRemainingRecoveryTime() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("%s: production step %s was interrupted while waiting for recovery",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void dealWithFailure(ProductionStep productionStep) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s: interrupted before production step %s could process failures",
                    this.threadName, productionStep.getName()));
            return;
        }

        logger.info(String.format("%s: deal with failure in production step %s", this.threadName,
                productionStep.getName()));

        if (isFailureStep(productionStep)) {
            logger.info(String.format("%s: there was a failure in production step %s", this.threadName,
                    productionStep.getName()));

            startRecovery(productionStep, true);
        }

        waitForRecovery(productionStep);
    }

    private boolean isFailureStep(ProductionStep productionStep) {
        logger.info(String.format("%s: determine if there was a random failure in %s", this.threadName,
                productionStep.getName()));
        return Math.random() < productionStep.getFailureProbability();
    }

    private void startRecovery(ProductionStep productionStep, boolean isFailureRecovery) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted before step %s could start recovery", this.threadName,
                    productionStep.getName()));
            return;
        }

        this.threadCount++;
        String recoveryThreadName = String.format("%s subthread %d.%d - recovery",
                this.threadName, this.threadParentNumber, this.threadCount);

        productionStep.setProductionStatus(ProductionStatus.RECOVERY);

        Thread recoveryThread = new Thread(
                new RecoveryRunnable(productionStep, isFailureRecovery, recoveryThreadName,
                        this.productionTimeConsumer, this.endThreadConsumer),
                recoveryThreadName);

        // TODO: Make a consumer to indicate that the thread should be deleted from the
        // map
        this.childrenThreads.put(recoveryThreadName, recoveryThread);

        synchronized (this) {
            logger.info(String.format("%s: start recovery for production step %s", this.threadName,
                    productionStep.getName()));
            recoveryThread.start();
        }
    }

    private void waitForMaintenance() {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted before production line %s could wait for maintenance",
                    this.threadName, this.productionLine.getName()));
            return;
        }

        logger.info(String.format("%s: check if maintenance time is needed for production line %s",
                this.threadName,
                this.productionLine.getName()));

        while (!Thread.currentThread().isInterrupted()
                && this.productionLine.maxNecessaryMaintenanceTimeInMinutes() > 0) {

            logger.info(String.format("%s: maintenance time is needed for production line %s", this.threadName,
                    this.productionLine.getName()));

            List<Robot> robotsNeedMaintenance = this.productionLine.getProductionSteps().stream()
                    .filter(Robot.class::isInstance).map(Robot.class::cast)
                    .filter(robot -> robot.getProductionStatus().equals(ProductionStatus.NEEDS_MAINTENANCE)).toList();

            startMaintenance(robotsNeedMaintenance);

            try {
                Thread.sleep(this.productionLine.maxNecessaryMaintenanceTimeInMinutes() * 1000);
            } catch (InterruptedException e) {
                logger.info(String.format("s%s: production line %s was interrupted while waiting for maintenance",
                        this.threadName,
                        this.productionLine.getName()));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startMaintenance(List<Robot> robots) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted before production line %s could start maintenance for robots",
                    this.threadName, this.productionLine.getName()));
            return;
        }

        for (Robot robot : robots) {
            this.threadCount++;
            String maintThreadName = String.format("%s child thread %d.%d - maintenance", this.threadName,
                    this.threadParentNumber,
                    this.threadCount);
            Thread performMaintenanceThread = new Thread(
                    new MaintenanceRunnable(robot, maintThreadName, this.productionTimeConsumer,
                            this.robotMaintenanceConsumer, this.endThreadConsumer),
                    maintThreadName);
            this.childrenThreads.put(maintThreadName, performMaintenanceThread);
            performMaintenanceThread.start();
        }
    }

    private void startProduction(ProductionStep productionStep) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted in production", this.threadName));
            return;
        }

        if (this.production.getNumberProducedCars() == 0 && productionStep instanceof Robot) {
            synchronized (this) {
                logger.info(
                        String.format("%s: first time robot %s in production -- start maintenance cycle",
                                this.threadName,
                                productionStep.getName()));
                startMaintenanceCountdown((Robot) productionStep);
            }
        }

        long remainingProductionTime = productionStep.getDurationInMinutes();
        logger.info(String.format("%s: production step %s has %d minutes of production left", this.threadName,
                productionStep.getName(), remainingProductionTime));

        while (!Thread.currentThread().isInterrupted() && remainingProductionTime > 0) {
            // logger.info(String.format("%s: production step %s in production",
            // this.threadName,
            // productionStep.getName()));
            try {
                synchronized (this) {
                    remainingProductionTime--;
                    // logger.info(String.format("%s: remaining production time %d for production
                    // step %s",
                    // this.threadName, remainingProductionTime, productionStep.getName()));
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info(String.format("%s: production step %s was interrupted during production",
                        this.threadName,
                        productionStep.getName()));
                Thread.currentThread().interrupt();
            }
        }

        if (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                logger.info(String.format("%s: production step %s finished production", this.threadName,
                        productionStep.getName()));
                logger.info(String.format("%s: saving production time for step %s",
                        this.threadName,
                        productionStep.getName()));
                ProductionTime productionTime = new ProductionTime(ProductionTimeType.PRODUCTION,
                        productionStep.getDurationInMinutes(), this.production);
                this.productionTimes.add(productionTime);
            }
        }

    }

    private void startMaintenanceCountdown(Robot robot) {
        if (Thread.currentThread().isInterrupted()) {
            logger.info(String.format("%s interrupted before robot %s could start maintenance countdown",
                    this.threadName, robot.getName()));
            return;
        }

        this.threadCount++;
        String waitMaintThreadName = String.format("%s child thread %d.%d - count down to maintenance", this.threadName,
                this.threadParentNumber,
                this.threadCount);
        // TODO -> provide cleanup consumer so that the thread is stopped and deleted
        // from the map
        Thread waitForMaintenanceThread = new Thread(
                new MaintenanceWaitingRunnable(robot, waitMaintThreadName, endThreadConsumer),
                waitMaintThreadName);
        this.childrenThreads.put(waitMaintThreadName, waitForMaintenanceThread);
        waitForMaintenanceThread.start();
    }

    private void cleanUp() {
        synchronized (this) {
            logger.info(String.format("%s: clean up production line %s", this.threadName,
                    this.production.getProductionLine().getName()));
            Thread.currentThread().interrupt();
            stopChildThreads();

            this.production.setEndTime(LocalDateTime.now());
            this.productionLine.setSimulationStatus(SimulationStatus.STOPPED);
            this.productionLine.setAllProductionStepStatus(ProductionStatus.WAITING);
            this.productionLine.resetAllProductionStepRecoveryTimes();
            this.saveProductionAndProductionTimesConsumer.accept(production, this.productionTimes);
        }
    }

    private void stopChildThreads() {
        for (Entry<String, Thread> entry : this.childrenThreads.entrySet()) {
            entry.getValue().interrupt();
        }

        this.childrenThreads.clear();
    }

}
