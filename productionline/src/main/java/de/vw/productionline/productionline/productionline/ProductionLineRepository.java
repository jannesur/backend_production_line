package de.vw.productionline.productionline.productionline;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionLineRepository extends JpaRepository<ProductionLine, UUID> {
    
}
