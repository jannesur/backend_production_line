package de.vw.productionline.productionline.employee;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
