package de.vw.productionline.productionline.station;

import java.util.List;
import java.util.Optional;

import de.vw.productionline.productionline.productionstep.ProductionStepRepository;

public interface StationRepository extends ProductionStepRepository<Station> {
    Optional<Station> findByName(String name);
    List<Station> findAllByProductionLineIsNull();
}
