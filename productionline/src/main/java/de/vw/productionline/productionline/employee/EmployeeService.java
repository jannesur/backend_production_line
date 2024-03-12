package de.vw.productionline.productionline.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeById(UUID uuid) {
        Optional<Employee> employee = employeeRepository.findById(uuid);
        if (employee.isEmpty()) {
            throw new ObjectNotFoundException("Employee not found");
        }
        return employee.get();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getAllEmployeesWithoutStation() {
        return employeeRepository.findAll().stream().filter(e -> e.getStation() == null).toList();
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID uuid) {
        employeeRepository.deleteById(uuid);
    }

}
