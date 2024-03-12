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
        Optional<Station> optional = this.stationRepository.getByName(station.getName());
        if (optional.isPresent()) {

        }
        return this.stationRepository.save(station);
    }

    public Station updateStation(Station station) {
        Optional<Station> optional = this.stationRepository.findById(station.getUuid());
        if (optional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", station.getUuid()));
        }

        return this.stationRepository.save(station);
    }

    public void deleteStationById(UUID uuid) {
        Optional<Station> optional = this.stationRepository.findById(uuid);
        if (optional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Station with UUID %s does not exist.", uuid));
        }

        this.stationRepository.delete(optional.get());
    }

}
