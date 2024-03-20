package de.vw.productionline.productionline.productionline;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionLineRepository extends JpaRepository<ProductionLine, String> {

    Optional<ProductionLine> findByName(String name);
}
