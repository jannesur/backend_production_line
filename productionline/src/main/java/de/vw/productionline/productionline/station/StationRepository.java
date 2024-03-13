package de.vw.productionline.productionline.station;

import java.util.Optional;

import de.vw.productionline.productionline.productionstep.ProductionStepRepository;

public interface StationRepository extends ProductionStepRepository<Station> {
    Optional<Station> getByName(String name);
}
