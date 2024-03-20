package de.vw.productionline.productionline.production;

import de.vw.productionline.productionline.productionline.VehicleModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel) {
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
                productionLineUuid, vehicleModel);
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/productionTimesFromOneProductionLine/{productionLineUuid}")
    public ResponseEntity<List<List<ProductionTime>>> getAllProductionTimesFromOneProductionLine(
            @PathVariable(value = "productionLineUuid") UUID productionLineUuid) {
        List<List<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimesFromOneProductionLine(productionLineUuid);
        return ResponseEntity.ok(productionTimeList);
    }

    @GetMapping("/productionTimesFromOneVehicleModel/{vehicleModel}")
    public List<List<ProductionTime>> getAllProductionTimesFromOneVehicleModel(
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel) {
        List<List<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimesFromOneVehicleModel(vehicleModel);
        return productionTimeList;
    }

    @GetMapping("/productionTimesFromOneProduction/{uuid}")
    public List<List<ProductionTime>> getProductionTimeForOneProduction(@PathVariable(value = "uuid") UUID uuid) {
        List<List<ProductionTime>> productionTimeList = productionService
                .getProductionTimeForOneProduction(uuid);
        return productionTimeList;
    }

    @GetMapping("/allProductionTimes")
    public List<List<ProductionTime>> getAllProductionTimes() {
        List<List<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimes();
        return productionTimeList;
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
