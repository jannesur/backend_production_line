package de.vw.productionline.productionline.robot;

import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Robot extends ProductionStep {

    private int maintenanceCycle;
    private int maintenanceTime;

    public Robot(UUID uuid, String name, long duration, double failureProbability, long timeToRecovery, int maintenanceCycle, int maintenanceTime) {
        super(uuid, name, duration, failureProbability, timeToRecovery);
        this.maintenanceCycle = maintenanceCycle;
        this.maintenanceTime = maintenanceTime;
    }

    public Robot(){}

    public int getMaintenanceCycle() {
        return maintenanceCycle;
    }

    public int getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceCycle(int maintenanceCycle) {
        this.maintenanceCycle = maintenanceCycle;
    }

    public void setMaintenanceTime(int maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Robot robot = (Robot) o;
        return maintenanceCycle == robot.maintenanceCycle && maintenanceTime == robot.maintenanceTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maintenanceCycle, maintenanceTime);
    }

    @Override
    public String toString() {
        return "Robot{" +
                "maintenanceCycle=" + maintenanceCycle +
                ", maintenanceTime=" + maintenanceTime +
                '}';
    }
}
