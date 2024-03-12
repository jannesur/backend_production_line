package de.vw.productionline.productionline.employee;

import org.springframework.stereotype.Controller;

@Controller
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
