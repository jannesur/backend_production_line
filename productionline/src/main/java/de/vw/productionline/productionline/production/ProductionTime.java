package de.vw.productionline.productionline.production;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductionTime {
    @Id
    private UUID uuid;
    private ProductionTimeType productionTimeType;
    private long durationInMinutes;
    @ManyToOne
    private Production production;
}
