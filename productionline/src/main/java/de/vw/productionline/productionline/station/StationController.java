package de.vw.productionline.productionline.station;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stations")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> getAllStations() {
        return this.stationService.getAllStations();
    }

    @GetMapping("{uuid}")
    public Station getStationById(@PathVariable UUID uuid) {
        return this.stationService.getStationById(uuid);
    }

    @GetMapping("without-production-line")
    public List<Station> getStationsWithoutProductionLine() {
        return this.stationService.getAllStationsNotInProductionLine();
    }

    @PostMapping
    public Station createStation(@RequestBody Station station) {
        return this.stationService.saveStation(station);
    }

    @PutMapping
    public Station updateStation(@RequestBody Station station) {
        return this.stationService.updateStation(station);
    }

    @DeleteMapping("{uuid}")
    public void deleteStationById(@PathVariable UUID uuid) {
        this.stationService.deleteStationById(uuid);
    }
}
