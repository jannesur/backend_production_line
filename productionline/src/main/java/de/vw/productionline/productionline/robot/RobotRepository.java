package de.vw.productionline.productionline.robot;

import java.util.List;
import java.util.Optional;

import de.vw.productionline.productionline.productionstep.ProductionStepRepository;

public interface RobotRepository extends ProductionStepRepository<Robot> {
    Optional<Robot> findByName(String name);
    List<Robot> findAllByProductionLineIsNull();
}
