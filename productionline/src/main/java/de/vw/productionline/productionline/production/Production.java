package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.VehicleModel;
import de.vw.productionline.productionline.productiontime.ProductionTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class Production {

    @Id
    private UUID uuid = UUID.randomUUID();

    @Transient
    @JsonIgnore
    private ProductionLine productionLine;

    private UUID productionLineUuid;
    private String productionLineName;

    @Enumerated(EnumType.STRING)
    private VehicleModel vehicleModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long numberProducedCars;

    @OneToMany(mappedBy = "production", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<ProductionTime> productionTimes = new HashSet<>();

    public Production(ProductionLine productionLine, UUID productionLineUuid, String productionLineName,
            VehicleModel vehicleModel, LocalDateTime startTime, LocalDateTime endTime, long numberProducedCars,
            Set<ProductionTime> productionTimes) {
        this.productionLine = productionLine;
        this.productionLineUuid = productionLineUuid;
        this.productionLineName = productionLineName;
        this.vehicleModel = vehicleModel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberProducedCars = numberProducedCars;
        this.productionTimes = productionTimes;
    }

    public Production(ProductionLine productionLine, LocalDateTime startTime, LocalDateTime endTime,
            long numberProducedCars) {
        this(productionLine, productionLine.getUuid(), productionLine.getName(), productionLine.getVehicleModel(),
                startTime, endTime, numberProducedCars, null);
    }

    public Production(ProductionLine productionLine) {
        this(productionLine, null, null, 0L);
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

    public UUID getProductionLineUuid() {
        return productionLineUuid;
    }

    public void setProductionLineUuid(UUID productionLineUuid) {
        this.productionLineUuid = productionLineUuid;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
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

    public Set<ProductionTime> getProductionTimes() {
        return productionTimes;
    }

    public void setProductionTimes(Set<ProductionTime> productionTimes) {
        this.productionTimes = productionTimes;
    }

    @Override
    public String toString() {
        return "Production [uuid=" + uuid + ", productionLineUuid=" + productionLineUuid + ", productionLineName="
                + productionLineName + ", vehicleModel=" + vehicleModel + ", startTime=" + startTime + ", endTime="
                + endTime + ", numberProducedCars=" + numberProducedCars + "]";
    }

}
