package de.vw.productionline.productionline.employee;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.station.Station;
import de.vw.productionline.productionline.station.StationRepository;
import de.vw.productionline.productionline.station.StationService;
import jakarta.annotation.PostConstruct;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private StationRepository stationRepository;

    private StationService stationService;

    public EmployeeService(EmployeeRepository employeeRepository, StationRepository stationRepository,
            StationService stationService) {
        this.employeeRepository = employeeRepository;
        this.stationRepository = stationRepository;
        this.stationService = stationService;
    }

    @PostConstruct
    private void initializeData() {
        System.out.println("Initializing employees");
        Station station = stationRepository.getByName("Painting").get();
        Employee employee1 = new Employee("Janne", station);
        Employee employee2 = new Employee("Adriana", station);
        Employee employee3 = new Employee("Tim", station);
        Employee employee4 = new Employee("Chris", null);
        Employee employee5 = new Employee("Leon", null);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);
        employeeRepository.save(employee5);

        Set<Employee> employees = new HashSet<>();
        employees.add(employee1);
        employees.add(employee2);
        employees.add(employee3);

        // station.setEmployees(employees);
        // stationRepository.save(station);
    }

    public Employee getEmployeeById(UUID uuid) {
        Optional<Employee> employee = employeeRepository.findById(uuid);
        if (employee.isEmpty()) {
            throw new ObjectNotFoundException("Employee not found");
        }
        return employee.get();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getAllEmployeesWithoutStation() {
        return employeeRepository.findAll().stream().filter(e -> e.getStation() == null).toList();
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID uuid) {
        Optional<Employee> employee = employeeRepository.findById(uuid);
        if (employee.isEmpty()) {
            throw new ObjectNotFoundException("Employee not found");
        }
        if (employee.get().getStation() != null) {
            Station station = employee.get().getStation();
            station.getEmployees().remove(employee.get());
            stationRepository.save(station);
        }
        employeeRepository.deleteById(uuid);
    }

    public Employee updatEmployee(UUID uuid, Employee employee) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(uuid);
        if (optionalEmployee.isEmpty()) {
            throw new ObjectNotFoundException("Employee not found.");
        }
        Employee existingEmployee = optionalEmployee.get();
        existingEmployee.setName(employee.getName());
        existingEmployee.setStation(employee.getStation());
        return employeeRepository.save(existingEmployee);
    }

}
