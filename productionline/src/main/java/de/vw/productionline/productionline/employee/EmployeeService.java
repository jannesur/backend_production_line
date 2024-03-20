package de.vw.productionline.productionline.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.productionstep.RecoveryRunnable;
import de.vw.productionline.productionline.station.Station;
import de.vw.productionline.productionline.station.StationRepository;
import de.vw.productionline.productionline.station.StationService;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private StationRepository stationRepository;

    private Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private StationService stationService;

    public EmployeeService(EmployeeRepository employeeRepository, StationRepository stationRepository,
            StationService stationService) {
        this.employeeRepository = employeeRepository;
        this.stationRepository = stationRepository;
        this.stationService = stationService;
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
        int testsizebefore = employeeRepository.findAll().size();
        if (employee.isEmpty()) {
            throw new ObjectNotFoundException("Employee not found");
        }
        if (employee.get().getStation() != null) {
            Station station = employee.get().getStation();
            boolean test = station.getEmployees().remove(employee.get());
            // employee.get().setStation(null);
            stationRepository.save(station);
        }
        employeeRepository.delete(employee.get());
        int testsizeafter = employeeRepository.findAll().size();
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
