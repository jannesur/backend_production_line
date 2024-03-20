package de.vw.productionline.productionline.production;

import de.vw.productionline.productionline.productionline.VehicleModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/production")
@CrossOrigin(origins = "http://localhost:5173/")
public class ProductionController {

    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @GetMapping
    public long getAllProducedCarsFromOneVehicleModel(VehicleModel vehicleModel){
        return 0;
    }
    @PostMapping("/start/{uuid}")
    public ResponseEntity<Void> startProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionService.startProduction(uuid);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/stop/{uuid}")
    public ResponseEntity<Void> stopProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionService.stopProduction(uuid);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/test")
    public void test() {
        this.productionService.testSaving();
    }

}
