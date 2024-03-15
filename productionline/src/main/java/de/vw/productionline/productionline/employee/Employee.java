package de.vw.productionline.productionline.employee;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.vw.productionline.productionline.station.Station;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Employee {

    @Id
    private UUID uuid = UUID.randomUUID();
    @NotBlank(message = "Employee name cannot be null")
    private String name;

    @JsonBackReference
    @Nullable
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Station station;

    public Employee(String name, Station station) {
        this.name = name;
        this.station = station;
    }

    public Employee() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Employee other = (Employee) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return "Employee [id=" + uuid + ", name=" + name + "]" + station;
    }

}
