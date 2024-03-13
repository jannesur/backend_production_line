package de.vw.productionline.productionline.productionline;

import java.util.List;
import java.util.UUID;

import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class ProductionLine {
    @Id
    private UUID uuid = UUID.randomUUID();
    private String name;
    private Status status;
    private SimulationStatus simulationStatus;
    private VehicleModel vehicleModel;
    @OneToMany(mappedBy = "productionLine")
    List<ProductionStep> productionSteps;

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
