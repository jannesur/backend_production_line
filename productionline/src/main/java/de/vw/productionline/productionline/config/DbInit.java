package de.vw.productionline.productionline.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.employee.EmployeeRepository;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineRepository;
import de.vw.productionline.productionline.productionline.Status;
import de.vw.productionline.productionline.productionline.VehicleModel;
import de.vw.productionline.productionline.productionstep.ProductionStep;
import de.vw.productionline.productionline.robot.Robot;
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
    private Logger logger = LoggerFactory.getLogger(DbInit.class);

    public DbInit(ProductionLineRepository productionLineRepository, EmployeeRepository employeeRepository,
            RobotRepository robotRepository, StationRepository stationRepository) {
        this.employeeRepository = employeeRepository;
        this.productionLineRepository = productionLineRepository;
        this.robotRepository = robotRepository;
        this.stationRepository = stationRepository;
        this.employeeRepository = employeeRepository;
    }

    // @PostConstruct
    private void initializeData() {
        // saveNewStations();
        // saveNewRobots();
        saveNewEmployees();
        // saveNewProductionLines();
        // assignEmployeesToStations();
        // assignStationsAndRobotsToProductionLines();
    }

    private void saveNewStations() {
        logger.info("Initializing stations");
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
        logger.info("Initializing employees");
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
        logger.info("Initializing robots.");
        Robot robot1 = new Robot("Robby Robot", 15l, 0.25, 20l, 120l, 15l);
        Robot robot2 = new Robot("Painting Robot", 10l, 0.01, 30l, 320l, 10l);
        Robot robot3 = new Robot("Wheels Robot", 1l, 0.2, 10l, 360l, 5l);
        Robot robot4 = new Robot("Bumper Robot", 150l, 0.001, 1l, 30l, 30l);
        Robot robot5 = new Robot("VW Robot", 25l, 0.4, 5l, 40l, 25l);

        robotRepository.save(robot1);
        robotRepository.save(robot2);
        robotRepository.save(robot3);
        robotRepository.save(robot4);
        robotRepository.save(robot5);
    }

    private void saveNewProductionLines() {
        logger.info("Initializing production lines.");
        // String name, VehicleModel vehicleModel
        ProductionLine productionLine1 = new ProductionLine("Production line 1", VehicleModel.GOLF);
        ProductionLine productionLine2 = new ProductionLine("Production line 2", VehicleModel.ID3);
        ProductionLine productionLine3 = new ProductionLine("Production line 3", VehicleModel.ID4);
        ProductionLine productionLine4 = new ProductionLine("Production line 4", VehicleModel.ID7);
        ProductionLine productionLine5 = new ProductionLine("Production line 5", VehicleModel.GOLF);

        productionLineRepository.save(productionLine1);
        productionLineRepository.save(productionLine2);
        productionLineRepository.save(productionLine3);
        productionLineRepository.save(productionLine4);
        productionLineRepository.save(productionLine5);
    }

    private void assignEmployeesToStations() {
        logger.info("Assigning employees to stations.");
        Set<Employee> employees = new HashSet<>();

        Employee paintEmployee = employeeRepository.findByName("Janne").get();
        Station paintStation = stationRepository.findByName("Painting").get();

        employees.add(paintEmployee);
        paintStation.setEmployees(employees);

        paintEmployee.setStation(paintStation);
        Employee e = employeeRepository.save(paintEmployee);
        System.out.println(e);

        Station s = stationRepository.save(paintStation);
        System.out.println(s);
    }

    private void assignStationsAndRobotsToProductionLines() {
        logger.info("Assigning stations and robots to production lines.");
        List<ProductionStep> productionSteps = new ArrayList();

        ProductionLine productionLine2 = productionLineRepository.findByName("Production line 2").get();
        Robot paintRobot = robotRepository.findByName("Painting Robot").get();
        paintRobot.setProductionLine(productionLine2);
        productionSteps.add(paintRobot);
        Station paintStation = stationRepository.findByName("Painting").get();
        paintStation.setProductionLine(productionLine2);
        productionSteps.add(paintStation);

        robotRepository.save(paintRobot);
        stationRepository.save(paintStation);
        productionLine2.setProductionSteps(productionSteps);
        productionLine2.setStatus(Status.READY);
        productionLineRepository.save(productionLine2);
    }

}
