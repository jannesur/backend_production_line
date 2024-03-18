package de.vw.productionline.productionline.productionline;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.vw.productionline.productionline.production.Production;
import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class ProductionLine {
    @Id
    private UUID uuid = UUID.randomUUID();

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private SimulationStatus simulationStatus;

    @Enumerated(EnumType.STRING)
    private VehicleModel vehicleModel;

    @OneToMany(mappedBy = "productionLine")
    @JsonManagedReference
    List<ProductionStep> productionSteps;
    @OneToOne
    private Production production;

    public ProductionLine(String name, Status status, SimulationStatus simulationStatus,
            VehicleModel vehicleModel) {
        this.name = name;
        this.status = status;
        this.simulationStatus = simulationStatus;
        this.vehicleModel = vehicleModel;
    }

    public ProductionLine(String name, VehicleModel vehicleModel) {
        this.name = name;
        this.status = Status.INCOMPLETE;
        this.simulationStatus = SimulationStatus.STOPPED;
        this.vehicleModel = vehicleModel;
    }

    public ProductionLine() {

    }

    public long maxNecessaryMaintenanceTimeInMinutes() {
        return this.productionSteps.stream()
                .filter(e -> e.getProductionStatus().equals(ProductionStatus.MAINTENANCE))
                .mapToLong(e -> e.getRemainingRecoveryTime()).max().orElse(0);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SimulationStatus getSimulationStatus() {
        return this.simulationStatus;
    }

    public void setSimulationStatus(SimulationStatus simulationStatus) {
        this.simulationStatus = simulationStatus;
    }

    public VehicleModel getVehicleModel() {
        return this.vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public List<ProductionStep> getProductionSteps() {
        return this.productionSteps;
    }

    public void setProductionSteps(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    @Override
    public String toString() {
        return "{" +
                " uuid='" + getUuid() + "'" +
                ", name='" + getName() + "'" +
                ", status='" + getStatus() + "'" +
                ", simulationStatus='" + getSimulationStatus() + "'" +
                ", vehicleModel='" + getVehicleModel() + "'" +
                "}";
    }

}
