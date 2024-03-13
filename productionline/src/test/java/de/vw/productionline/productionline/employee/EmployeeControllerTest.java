package de.vw.productionline.productionline.employee;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.station.Station;
import de.vw.productionline.productionline.station.StationRepository;

@SpringBootTest
public class EmployeeControllerTest {

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    public void setUp() {
        Station station = new Station();
        station.setName("Station 1");
        stationRepository.save(station);

        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee("Adriana", null);
        Employee employee2 = new Employee("Janne", null);
        Employee employee3 = new Employee("Tim", null);
        Employee employee4 = new Employee("Chris", station);

        employees.add(employee);
        employees.add(employee2);
        employees.add(employee3);
        employees.add(employee4);

        employeeRepository.saveAll(employees);
    }

    @Test
    public void testGetAllEmployees() {
        // given
        int expectedAmountEmployees = 4;

        // when
        List<Employee> employees = employeeController.getAllEmployees();

        // then
        assertEquals(expectedAmountEmployees, employees.size());
    }

    @Test
    public void testGetAllEmployeesWithoutStation() {
        // given
        int expectedAmountEmployeesWithoutStation = 3;

        // when
        List<Employee> employeesWithoutStation = employeeController.getAllEmployeesWithoutStation();

        // then
        assertEquals(expectedAmountEmployeesWithoutStation, employeesWithoutStation.size());
    }

    @Test
    public void testGetEmployeeByIdValid() {
        // given
        Employee employee = employeeController.getAllEmployees().get(0);
        UUID employeeId = employee.getId();

        // when
        Employee foundEmployee = employeeController.getEmployeeById(employeeId);

        // then
        assertEquals(employee, foundEmployee);
    }

    @Test
    public void testGetEmployeeByIdInvalid() {
        // given
        UUID employeeId = UUID.randomUUID();

        // when then
        assertThrows(ObjectNotFoundException.class, () -> {
            employeeController.getEmployeeById(employeeId);
        });
    }

    @Test
    public void testCreateEmployeeValid() {
        // given
        Employee validEmployee = new Employee();
        int expectedAmountEmployees = employeeController.getAllEmployees().size() + 1;
        validEmployee.setName("MR X");
        validEmployee.setStation(null);

        // when
        Employee savedValidEmployee = employeeController.createEmployee(validEmployee);

        // then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().size());
        Assert.notNull(savedValidEmployee, "validEmployee should not be null");
        Assert.isTrue(savedValidEmployee.getName().equals("MR X"), "validEmployee name should match");
        Assert.isNull(savedValidEmployee.getStation(), "validEmployee station should be null");
    }

    @Test
    public void testCreateEmployeeInvalid() {
        // given
        Employee invalidEmployee = new Employee();
        invalidEmployee.setName(null);
        int expectedAmountEmployees = employeeController.getAllEmployees().size();

        // when then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().size());
        assertThrows(TransactionSystemException.class, () -> {
            employeeController.createEmployee(invalidEmployee);
        });
    }

    @Test
    public void testDeleteEmployee() {
        // given
        Employee employee = employeeController.getAllEmployees().get(0);
        UUID employeeId = employee.getId();
        int expectedAmountEmployees = employeeController.getAllEmployees().size() - 1;

        // when
        employeeController.deleteEmployee(employeeId);

        // then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().size());
        assertThrows(ObjectNotFoundException.class, () -> {
            employeeController.getEmployeeById(employeeId);
        });
    }
}
