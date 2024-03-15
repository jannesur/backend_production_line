package de.vw.productionline.productionline.employee;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    public Optional<Employee> findByName(String name);

    public void deleteByUuid(UUID uuid);
}
