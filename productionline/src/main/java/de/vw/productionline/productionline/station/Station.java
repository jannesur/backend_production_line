package de.vw.productionline.productionline.station;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
public class Station extends ProductionStep {
    @OneToMany(mappedBy = "station", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Employee> employees = new HashSet<>();

    public Station(String name, long duration, double failureProbability, long timeToRecovery, int step,
            ProductionLine productionLine, Set<Employee> employees) {
        super(name, duration, failureProbability, timeToRecovery, step, productionLine);
        this.employees = employees;
    }

    public Station(String name, long duration, double failureProbability, long timeToRecovery, int step,
            Set<Employee> employees) {
        super(name, duration, failureProbability, timeToRecovery, step, null);
        this.employees = employees;
    }

    public Station(String name, long duration, double failureProbability, long timeToRecovery) {
        this(name, duration, failureProbability, timeToRecovery, 0, null);
    }

    public Station() {
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "Station [id=" + super.getUuid() + ", name=" + super.getName() + "]";
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
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
        return super.equals(obj);
    }

}
