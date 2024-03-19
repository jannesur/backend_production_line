package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.UUID;

import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.VehicleModel;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Production {
    @Id
    private UUID uuid = UUID.randomUUID();
    @Transient
    private ProductionLine productionLine;
    private VehicleModel vehicleModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long numberProducedCars;

    @Transient
    private ProductionStep currentProductionStep;

    public Production(ProductionLine productionLine, LocalDateTime startTime, LocalDateTime endTime,
            long numberProducedCars, ProductionStep currentProductionStep) {
        this.productionLine = productionLine;
        this.vehicleModel = productionLine.getVehicleModel();
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberProducedCars = numberProducedCars;
        this.currentProductionStep = currentProductionStep;
    }

    public Production(ProductionLine productionLine) {
        this(productionLine, null, null, 0L, null);
    }

    public Production() {
    }

    public void incrementProducedCars() {
        this.numberProducedCars++;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ProductionLine getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(ProductionLine productionLine) {
        this.productionLine = productionLine;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getNumberProducedCars() {
        return numberProducedCars;
    }

    public void setNumberProducedCars(long numberProducedCars) {
        this.numberProducedCars = numberProducedCars;
    }

    public ProductionStep getCurrentProductionStep() {
        return currentProductionStep;
    }

    public void setCurrentProductionStep(ProductionStep currentProductionStep) {
        this.currentProductionStep = currentProductionStep;
    }

    @Override
    public String toString() {
        return "Production [uuid=" + uuid + ", productionLine=" + productionLine + ", startTime=" + startTime
                + ", endTime=" + endTime + ", numberProducedCars=" + numberProducedCars + ", currentProductionStep="
                + currentProductionStep + "]";
    }

}
