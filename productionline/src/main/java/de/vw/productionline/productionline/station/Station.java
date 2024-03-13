package de.vw.productionline.productionline.station;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Station extends ProductionStep {
    @OneToMany(mappedBy = "station")
    @JsonManagedReference
    private Set<Employee> employees = new HashSet<>();

    public Station(String name, long duration, double failureProbability, long timeToRecovery,
            ProductionLine productionLine, Set<Employee> employees) {
        super(name, duration, failureProbability, timeToRecovery, productionLine);
        this.employees = employees;
    }

    public Station(String name, long duration, double failureProbability, long timeToRecovery,
            Set<Employee> employees) {
        super(name, duration, failureProbability, timeToRecovery, null);
        this.employees = employees;
    }

    public Station(String name, long duration, double failureProbability, long timeToRecovery) {
        this(name, duration, failureProbability, timeToRecovery, null);
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
        return "Station [employees=" + employees + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((employees == null) ? 0 : employees.hashCode());
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
        Station other = (Station) obj;
        if (employees == null) {
            if (other.employees != null)
                return false;
        } else if (!employees.equals(other.employees))
            return false;
        return true;
    }

}
