package de.vw.productionline.productionline.employee;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EmployeeIntegrationTest {
    // @Autowired
    // private TestRestTemplate template;

    // @Autowired
    // private EmployeeRepository employeeRepository;

    // @Test
    // void createEmployee() {
    // long originalSize = employeeRepository.count();
    // Employee emp = new Employee("Test", null);
    // ResponseEntity<Employee> newEmp = this.template.postForEntity("/employees",
    // emp, Employee.class);
    // System.out.println(emp);
    // System.out.println(newEmp.getBody());
    // Assertions.assertEquals(emp.getName(), newEmp.getBody().getName());
    // Assertions.assertEquals(originalSize + 1, employeeRepository.count());
    // }
}
