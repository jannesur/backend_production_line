package de.vw.productionline.productionline.station;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.employee.Employee;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Set<Employee> getEmployeesByStationId(UUID uuid) {
        Optional<Station> optional = stationRepository.findById(uuid);
        if (optional.isPresent()) {
            return optional.get().getEmployees();
        }

        return new HashSet<>();
    }

}
