package de.vw.productionline.productionline.productionline;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import de.vw.productionline.productionline.robot.Robot;

public class ProductionLineUnitTest {
    @Test
    void findsMaxNeededMaintenanceTime() {
        Robot robot1 = new Robot("A", 10, 0.1, 10, 10, 100);
        Robot robot2 = new Robot("B", 20, 0.2, 20, 20, 200);
        Robot robot3 = new Robot("C", 30, 0.3, 30, 30, 300);

        robot1.setProductionStatus(ProductionStatus.NEEDS_MAINTENANCE);
        robot2.setProductionStatus(ProductionStatus.NEEDS_MAINTENANCE);
        robot3.setProductionStatus(ProductionStatus.NEEDS_MAINTENANCE);

        List<ProductionStep> robots = new ArrayList<>();
        robots.add(robot1);
        robots.add(robot2);
        robots.add(robot3);

        ProductionLine productionLine = new ProductionLine("Test production", VehicleModel.GOLF);
        productionLine.setProductionSteps(robots);

        Assertions.assertEquals(300, productionLine.maxNecessaryMaintenanceTimeInMinutes());
    }
}
