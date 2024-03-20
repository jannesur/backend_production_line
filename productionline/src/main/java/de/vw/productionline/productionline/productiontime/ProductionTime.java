package de.vw.productionline.productionline.productiontime;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.vw.productionline.productionline.production.Production;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductionTime {

    @Id
    private UUID uuid = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    private ProductionTimeType productionTimeType;

    private long durationInMinutes;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Production production;

    public ProductionTime(ProductionTimeType productionTimeType, long durationInMinutes, Production production) {
        this.productionTimeType = productionTimeType;
        this.durationInMinutes = durationInMinutes;
        this.production = production;
    }

    public ProductionTime() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ProductionTimeType getProductionTimeType() {
        return productionTimeType;
    }

    public void setProductionTimeType(ProductionTimeType productionTimeType) {
        this.productionTimeType = productionTimeType;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    @Override
    public String toString() {
        return "ProductionTime [uuid=" + uuid + ", productionTimeType=" + productionTimeType + ", durationInMinutes="
                + durationInMinutes + "]";
    }

}
