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

    @GetMapping("/carsFromOneVehicleModel/{vehicleModel}")
    public ResponseEntity<Long> getAllProducedCarsFromOneVehicleModel(
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel){
        long producedCars = productionService.getAllProducedCarsFromOneVehicleModel(vehicleModel);
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/allCars")
    public ResponseEntity<Long> getAllProducedCars() {
        long producedCars = productionService.getAllProducedCars();
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/carsFromOneProductionLine/{productionLineUuid}")
    public ResponseEntity<Long> getAllProducedCarsFromOneProductionLine(
            @PathVariable(value = "productionLineUuid") UUID productionLineUuid) {
        long producedCars = productionService.getAllProducedCarsFromOneProductionLine(productionLineUuid);
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/carsFromOneProductionLineAndOneVehicleModel/{productionLineUuid}/{vehicleModel}")
    public ResponseEntity<Long> getAllProducedCarsFromOneProductionLineForOneVehicleModel(
            @PathVariable(value = "productionLineUuid") UUID productionLineUuid,
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel) {
        long producedCars = productionService.getAllProducedCarsFromOneProductionLineForOneVehicleModel(
                productionLineUuid,vehicleModel);
        return ResponseEntity.ok(producedCars);
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
