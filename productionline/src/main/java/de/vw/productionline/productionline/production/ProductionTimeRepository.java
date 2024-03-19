package de.vw.productionline.productionline.production;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionTimeRepository extends JpaRepository<ProductionTime, UUID> {

}
