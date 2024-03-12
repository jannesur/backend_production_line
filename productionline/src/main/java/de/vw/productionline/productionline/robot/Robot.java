package de.vw.productionline.productionline.robot;

import java.util.UUID;

import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;

@Entity
public class Robot extends ProductionStep {
    private long maintenanceCycleInMinutes;
    private long maintenanceTimeInMinutes;

    public Robot(UUID uuid, String name, long duration, double failureProbability, long timeToRecovery,
            long maintenanceCycleInMinutes, long maintenanceTimeInMinutes) {
        super(uuid, name, duration, failureProbability, timeToRecovery);
        this.maintenanceCycleInMinutes = maintenanceCycleInMinutes;
        this.maintenanceTimeInMinutes = maintenanceTimeInMinutes;
    }

    public Robot() {
    }

    public long getMaintenanceCycleInMinutes() {
        return maintenanceCycleInMinutes;
    }

    public void setMaintenanceCycleInMinutes(long maintenanceCycleInMinutes) {
        this.maintenanceCycleInMinutes = maintenanceCycleInMinutes;
    }

    public long getMaintenanceTimeInMinutes() {
        return maintenanceTimeInMinutes;
    }

    public void setMaintenanceTimeInMinutes(long maintenanceTimeInMinutes) {
        this.maintenanceTimeInMinutes = maintenanceTimeInMinutes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (maintenanceCycleInMinutes ^ (maintenanceCycleInMinutes >>> 32));
        result = prime * result + (int) (maintenanceTimeInMinutes ^ (maintenanceTimeInMinutes >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Robot other = (Robot) obj;
        if (maintenanceCycleInMinutes != other.maintenanceCycleInMinutes)
            return false;
        if (maintenanceTimeInMinutes != other.maintenanceTimeInMinutes)
            return false;
        return true;
    }

}
