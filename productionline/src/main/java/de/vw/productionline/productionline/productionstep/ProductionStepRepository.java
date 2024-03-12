package de.vw.productionline.productionline.productionstep;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionStepRepository extends JpaRepository<ProductionStep, UUID> {

}
