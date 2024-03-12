package de.vw.productionline.productionline.employee;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("employees")
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping()
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("without-station")
    public List<Employee> getAllEmployeesWithoutStation() {
        return employeeService.getAllEmployeesWithoutStation();
    }

    @GetMapping("{uuid}")
    public Employee getEmployeeById(@PathVariable(value = "uuid") UUID uuid) {
        return employeeService.getEmployeeById(uuid);
    }

    @PostMapping()
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @DeleteMapping("{uuid}")
    public void deleteEmployee(@PathVariable(value = "uuid") UUID uuid) {
        employeeService.deleteEmployee(uuid);
    }

    // @PutMapping("{uuid}")
    // public Employee updateEmployee(@PathVariable(value = "uuid") UUID uuid,
    // @RequestBody Employee employee) {
    // return employeeService.updateEmployee(uuid, employee);
    // }

}
