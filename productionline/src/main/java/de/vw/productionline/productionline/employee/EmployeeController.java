package de.vw.productionline.productionline.employee;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
