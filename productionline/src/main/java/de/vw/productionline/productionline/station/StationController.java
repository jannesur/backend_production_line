package de.vw.productionline.productionline.station;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "http://localhost:5173/")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(this.stationService.getAllStations());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Station> getStationById(@PathVariable String uuid) {
        return ResponseEntity.ok(this.stationService.getStationById(uuid));
    }

    @GetMapping("/without-production-line")
    public ResponseEntity<List<Station>> getStationsWithoutProductionLine() {
        return ResponseEntity.ok(this.stationService.getAllStationsNotInProductionLine());
    }

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        return new ResponseEntity<>(this.stationService.saveStation(station), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Station> updateStation(@RequestBody Station station) {
        return ResponseEntity.ok(this.stationService.updateStation(station));
    }

    @PutMapping("/add-employee")
    public ResponseEntity<Station> addEmployeeToStation(@RequestParam String employeeUuid,
            @RequestParam String stationUuid) {
        return ResponseEntity.ok(this.stationService.addEmployeeToStation(employeeUuid, stationUuid));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteStationById(@PathVariable String uuid) {
        this.stationService.deleteStationById(uuid);
        return ResponseEntity.ok(null);
    }
}
