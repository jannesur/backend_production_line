package de.vw.productionline.productionline.station;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

}
