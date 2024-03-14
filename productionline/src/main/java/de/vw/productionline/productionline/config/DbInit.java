package de.vw.productionline.productionline.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.employee.EmployeeRepository;
import de.vw.productionline.productionline.productionline.ProductionLineRepository;
import de.vw.productionline.productionline.robot.RobotRepository;
import de.vw.productionline.productionline.station.Station;
import de.vw.productionline.productionline.station.StationRepository;
import jakarta.annotation.PostConstruct;

@Component
public class DbInit {
    private EmployeeRepository employeeRepository;
    private ProductionLineRepository productionLineRepository;
    private RobotRepository robotRepository;
    private StationRepository stationRepository;

    public DbInit(EmployeeRepository employeeRepository, ProductionLineRepository productionLineRepository,
            RobotRepository robotRepository, StationRepository stationRepository) {
        this.employeeRepository = employeeRepository;
        this.productionLineRepository = productionLineRepository;
        this.robotRepository = robotRepository;
        this.stationRepository = stationRepository;
    }

    @PostConstruct
    private void initializeData() {
        saveNewStations();
        saveNewRobots();
        saveNewEmployees();
        saveNewProductionLines();
        assignEmployeesToStations();
        assignStationsAndRobotsToProductionLines();
    }

    private void saveNewStations() {
        System.out.println("Initializing stations");
        Station station1 = new Station("Painting", 10l, 0.5, 15);
        Station station2 = new Station("Wheel", 1l, 0.01, 10);
        Station station3 = new Station("Tires", 5l, 0.05, 150);
        Station station4 = new Station("Body parts", 15l, 0.1, 10);
        Station station5 = new Station("Other stuff", 100l, 0.2, 1);

        stationRepository.save(station1);
        stationRepository.save(station2);
        stationRepository.save(station3);
        stationRepository.save(station4);
        stationRepository.save(station5);
    }

    private void saveNewEmployees() {
        System.out.println("Initializing employees");
        Employee employee1 = new Employee("Janne", null);
        Employee employee2 = new Employee("Adriana", null);
        Employee employee3 = new Employee("Tim", null);
        Employee employee4 = new Employee("Chris", null);
        Employee employee5 = new Employee("Leon", null);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);
        employeeRepository.save(employee5);
    }

    private void saveNewRobots() {

    }

    private void saveNewProductionLines() {

    }

    private void assignEmployeesToStations() {
        Set<Employee> employees = new HashSet<>();

        Employee printEmployee = employeeRepository.getByName("Janne").get();
        Station printStation = stationRepository.getByName("Painting").get();

        employees.add(printEmployee);
        printStation.setEmployees(employees);

        printEmployee.setStation(printStation);
        Employee e = employeeRepository.save(printEmployee);
        System.out.println(e);

        Station s = stationRepository.save(printStation);
        System.out.println(s);
    }

    private void assignStationsAndRobotsToProductionLines() {

    }

}
