package de.vw.productionline.productionline.employee;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("employees")
@CrossOrigin(origins = "http://localhost:5173/")
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/without-station")
    public ResponseEntity<List<Employee>> getAllEmployeesWithoutStation() {
        return ResponseEntity.ok(employeeService.getAllEmployeesWithoutStation());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "uuid") String uuid) {
        return ResponseEntity.ok(employeeService.getEmployeeById(uuid));
    }

    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return new ResponseEntity<>(employeeService.createEmployee(employee), HttpStatus.CREATED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable(value = "uuid") String uuid) {
        employeeService.deleteEmployee(uuid);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String uuid, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updatEmployee(uuid, employee));
    }

}
