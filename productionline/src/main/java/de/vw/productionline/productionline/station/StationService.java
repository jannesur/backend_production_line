package de.vw.productionline.productionline.station;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getAllStations() {
        return this.stationRepository.findAll();
    }

    public Station getStationById(UUID uuid) {
        Optional<Station> optional = this.stationRepository.findById(uuid);
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new ObjectNotFoundException(String.format("Station with UUID %s", uuid));
    }

    public List<Station> getAllStationsNotInProductionLine() {
        List<Station> stations = this.stationRepository.findAll();
        return stations.stream().filter(station -> station.getProductionLine() != null).toList();
    }

}
