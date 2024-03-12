package de.vw.productionline.productionline.station;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import jakarta.persistence.OneToMany;

public class Station extends ProductionStep {
    @OneToMany(mappedBy = "station")
    @JsonBackReference
    private Set<Employee> employees;

    public Station(UUID uuid, String name, long duration, double failureProbability, long timeToRecovery,
            Set<Employee> employees) {
        super(uuid, name, duration, failureProbability, timeToRecovery);
        this.employees = employees;
    }

    public Station() {
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
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
