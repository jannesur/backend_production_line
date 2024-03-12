package de.vw.productionline.productionline.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("employees")
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("all")
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            throw new IllegalArgumentException("No employees found");
        }
        return employeeService.getAllEmployees();
    }

    @GetMapping("without-station")
    public List<Employee> getAllEmployeesWithoutStation() {
        List<Employee> employees = employeeService.getAllEmployeesWithoutStation();
        if (employees.isEmpty()) {
            throw new IllegalArgumentException("No employees without station found");
        }
        return employees;
    }

    @GetMapping("id")
    public Employee getEmployeeById(@RequestParam UUID uuid) {
        Optional<Employee> employee = employeeService.getEmployeeById(uuid);
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("Employee not found");
        }
        return employee.get();
    }
}
