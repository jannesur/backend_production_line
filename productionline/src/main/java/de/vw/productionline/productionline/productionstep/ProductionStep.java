package de.vw.productionline.productionline.productionstep;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.vw.productionline.productionline.productionline.ProductionLine;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;

//TODO: fix constructors to reflect new attributes!!
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ProductionStep {
    @Id
    private UUID uuid = UUID.randomUUID();
    private String name;
    private long durationInMinutes;
    private double failureProbability;
    private long timeToRecovery;
    private int step;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    private ProductionLine productionLine;

    @Enumerated(EnumType.STRING)
    private ProductionStatus productionStatus;
    private long remainingRecoveryTime;

    protected ProductionStep(String name, long duration, double failureProbability, long timeToRecovery,
            int step, ProductionLine productionLine) {
        this.name = name;
        this.durationInMinutes = duration;
        this.failureProbability = failureProbability;
        this.timeToRecovery = timeToRecovery;
        this.step = step;
        this.productionLine = productionLine;
        this.productionStatus = ProductionStatus.WAITING;
    }

    protected ProductionStep(String name, long duration, double failureProbability, long timeToRecovery,
            ProductionLine productionLine) {
        this.name = name;
        this.durationInMinutes = duration;
        this.failureProbability = failureProbability;
        this.timeToRecovery = timeToRecovery;
        this.productionLine = productionLine;
        this.productionStatus = ProductionStatus.WAITING;
    }

    protected ProductionStep() {
    }

    public long reduceRecoveryTimeByOneMinute() {
        return --this.remainingRecoveryTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long duration) {
        this.durationInMinutes = duration;
    }

    public double getFailureProbability() {
        return failureProbability;
    }

    public void setFailureProbability(double failureProbability) {
        this.failureProbability = failureProbability;
    }

    public long getTimeToRecovery() {
        return timeToRecovery;
    }

    public void setTimeToRecovery(long timeToRecovery) {
        this.timeToRecovery = timeToRecovery;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public ProductionLine getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(ProductionLine productionLine) {
        this.productionLine = productionLine;
    }

    public ProductionStatus getProductionStatus() {
        return productionStatus;
    }

    public void setProductionStatus(ProductionStatus productionStatus) {
        this.productionStatus = productionStatus;
    }

    public long getRemainingRecoveryTime() {
        return remainingRecoveryTime;
    }

    public void setRemainingRecoveryTime(long remainingRecoveryTime) {
        this.remainingRecoveryTime = remainingRecoveryTime;
    }

    @Override
    public String toString() {
        return "ProductionStep [uuid=" + uuid + ", name=" + name + ", durationInMinutes=" + durationInMinutes
                + ", failureProbability=" + failureProbability + ", timeToRecovery=" + timeToRecovery
                + ", productionLine=" + productionLine + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (durationInMinutes ^ (durationInMinutes >>> 32));
        long temp;
        temp = Double.doubleToLongBits(failureProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (timeToRecovery ^ (timeToRecovery >>> 32));
        result = prime * result + ((productionLine == null) ? 0 : productionLine.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductionStep other = (ProductionStep) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (durationInMinutes != other.durationInMinutes)
            return false;
        if (Double.doubleToLongBits(failureProbability) != Double.doubleToLongBits(other.failureProbability))
            return false;
        if (timeToRecovery != other.timeToRecovery)
            return false;
        if (productionLine == null) {
            if (other.productionLine != null)
                return false;
        } else if (!productionLine.equals(other.productionLine))
            return false;
        return true;
    }

}
