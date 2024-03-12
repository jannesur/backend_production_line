package de.vw.productionline.productionline.productionstep;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionStepRepository<T extends ProductionStep> extends JpaRepository<T, UUID> {

}
