package de.vw.productionline.productionline.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
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

        Employee employee = new Employee("Adriana", null);
        Employee employee2 = new Employee("Janne", null);
        Employee employee3 = new Employee("Tim", null);
        Employee employee4 = new Employee("Chris", station);

        employeeRepository.save(employee);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Number of stations in db: " + stationRepository.count());
        System.out.println("Number of employees in db: " + employeeRepository.count());
        // AFTER
        // stationRepository.deleteAll();
        System.out.println("Number of employees in db: " + employeeRepository.count());
        // employeeRepository.deleteAll();
    }

    @Test
    public void testGetAllEmployees() {
        // given
        long expectedAmountEmployees = employeeRepository.count();

        // when
        List<Employee> employees = employeeController.getAllEmployees().getBody();

        // then
        assertEquals(expectedAmountEmployees, employees.size());
    }

    @Test
    public void testGetAllEmployeesWithoutStation() {
        // given
        long expectedAmountEmployeesWithoutStation = employeeRepository.findAll().stream()
                .filter(employee -> employee.getStation() == null).count();

        // when
        List<Employee> employeesWithoutStation = employeeController.getAllEmployeesWithoutStation().getBody();

        // then
        assertEquals(expectedAmountEmployeesWithoutStation, employeesWithoutStation.size());
    }

    @Test
    public void testGetEmployeeByIdValid() {
        // given
        Employee employee = employeeController.getAllEmployees().getBody().get(0);
        UUID employeeId = employee.getUuid();

        // when
        Employee foundEmployee = employeeController.getEmployeeById(employeeId).getBody();

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
        int expectedAmountEmployees = employeeController.getAllEmployees().getBody().size() + 1;
        validEmployee.setName("MR X");
        validEmployee.setStation(null);

        // when
        Employee savedValidEmployee = employeeController.createEmployee(validEmployee).getBody();

        // then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().getBody().size());
        Assert.notNull(savedValidEmployee, "validEmployee should not be null");
        Assert.isTrue(savedValidEmployee.getName().equals("MR X"), "validEmployee name should match");
        Assert.isNull(savedValidEmployee.getStation(), "validEmployee station should be null");
    }

    @Test
    public void testCreateEmployeeInvalid() {
        // given
        Employee invalidEmployee = new Employee();
        invalidEmployee.setName(null);
        int expectedAmountEmployees = employeeController.getAllEmployees().getBody().size();

        // when then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().getBody().size());
        assertThrows(TransactionSystemException.class, () -> {
            employeeController.createEmployee(invalidEmployee);
        });
    }

    @Test
    public void testDeleteEmployee() {
        // given
        Employee employee = employeeController.getAllEmployees().getBody().get(0);
        UUID employeeId = employee.getUuid();
        int expectedAmountEmployees = employeeController.getAllEmployees().getBody().size() - 1;

        // when
        employeeController.deleteEmployee(employeeId);

        // then
        assertEquals(expectedAmountEmployees, employeeController.getAllEmployees().getBody().size());
        assertThrows(ObjectNotFoundException.class, () -> {
            employeeController.getEmployeeById(employeeId);
        });
    }

}
