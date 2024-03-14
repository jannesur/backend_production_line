package de.vw.productionline.productionline.station;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.employee.Employee;
import de.vw.productionline.productionline.employee.EmployeeRepository;
import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import jakarta.annotation.PostConstruct;

@Service
public class StationService {
    private StationRepository stationRepository;

    private EmployeeRepository employeeRepository;

    public StationService(StationRepository stationRepository, EmployeeRepository employeeRepository) {
        this.stationRepository = stationRepository;
        this.employeeRepository = employeeRepository;
    }

    // @PostConstruct
    // private void initializeData() {
    //     System.out.println("Initializing stations");
    //     Station station1 = new Station("Painting", 10l, 0.5, 15);
    //     Station station2 = new Station("Wheel", 1l, 0.01, 10);
    //     Station station3 = new Station("Tires", 5l, 0.05, 150);
    //     Station station4 = new Station("Body parts", 15l, 0.1, 10);
    //     Station station5 = new Station("Other stuff", 100l, 0.2, 1);

    //     stationRepository.save(station1);
    //     stationRepository.save(station2);
    //     stationRepository.save(station3);
    //     stationRepository.save(station4);
    //     stationRepository.save(station5);
    // }

    public List<Station> getAllStations() {
        return this.stationRepository.findAll();
    }

    public Station getStationById(UUID uuid) {
        Optional<Station> optional = this.stationRepository.findById(uuid);
        if (optional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", uuid));
        }

        return optional.get();
    }

    public List<Station> getAllStationsNotInProductionLine() {
        List<Station> stations = this.stationRepository.findAll();
        return stations.stream().filter(station -> station.getProductionLine() != null).toList();
    }

    public Station saveStation(Station station) {
        return this.stationRepository.save(station);
    }

    public Station updateStation(Station station) {
        Optional<Station> updatedStation = this.stationRepository.findById(station.getUuid());
        if (updatedStation.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", station.getUuid()));
        }
        return this.stationRepository.save(station);
    }

    public Station addEmployeeToStation(UUID employeeUuid, UUID stationUuid) {
        Optional<Employee> employeeOptional = this.employeeRepository.findById(employeeUuid);
        if (employeeOptional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Employee with UUID %s does not exist.", employeeUuid));
        }
        Optional<Station> stationOptional = this.stationRepository.findById(stationUuid);
        if (stationOptional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", stationUuid));
        }
        Employee employee = employeeOptional.get();
        Station station = stationOptional.get();
        employee.setStation(station);
        station.getEmployees().add(employee);
        this.employeeRepository.save(employee);
        return station;
    }

    public void deleteStationById(UUID uuid) {
        Optional<Station> station = this.stationRepository.findById(uuid);
        if (station.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", uuid));
        }
        if (!station.get().getEmployees().isEmpty()) {
            for (Employee employee : station.get().getEmployees()) {
                employee.setStation(null);
                this.employeeRepository.save(employee);
            }
        }
        this.stationRepository.delete(station.get());
    }

}
