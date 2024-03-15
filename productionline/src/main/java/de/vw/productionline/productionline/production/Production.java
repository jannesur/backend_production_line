package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.UUID;

import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Production {
    private UUID uuid = UUID.randomUUID();
    @OneToOne
    private ProductionLine productionLine;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long numberProducedCars;
    private ProductionStep currentProductionStep;

}
