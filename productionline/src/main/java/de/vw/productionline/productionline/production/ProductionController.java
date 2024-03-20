package de.vw.productionline.productionline.production;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.vw.productionline.productionline.productionline.VehicleModel;
import de.vw.productionline.productionline.productiontime.ProductionTime;

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
            @PathVariable(value = "productionLineUuid") String productionLineUuid) {
        long producedCars = productionService.getAllProducedCarsFromOneProductionLine(productionLineUuid);
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/carsFromOneProductionLineAndOneVehicleModel/{productionLineUuid}/{vehicleModel}")
    public ResponseEntity<Long> getAllProducedCarsFromOneProductionLineForOneVehicleModel(
            @PathVariable(value = "productionLineUuid") String productionLineUuid,
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel) {
        long producedCars = productionService.getAllProducedCarsFromOneProductionLineForOneVehicleModel(
                productionLineUuid, vehicleModel);
        return ResponseEntity.ok(producedCars);
    }

    @GetMapping("/productionTimesFromOneProductionLine/{productionLineUuid}")
    public ResponseEntity<List<Set<ProductionTime>>> getAllProductionTimesFromOneProductionLine(
            @PathVariable(value = "productionLineUuid") String productionLineUuid) {
        List<Set<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimesFromOneProductionLine(productionLineUuid);
        return ResponseEntity.ok(productionTimeList);
    }

    @GetMapping("/productionTimesFromOneVehicleModel/{vehicleModel}")
    public List<Set<ProductionTime>> getAllProductionTimesFromOneVehicleModel(
            @PathVariable(value = "vehicleModel") VehicleModel vehicleModel) {
        List<Set<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimesFromOneVehicleModel(vehicleModel);
        return productionTimeList;
    }

    @GetMapping("/productionTimesFromOneProduction/{uuid}")
    public List<Set<ProductionTime>> getProductionTimeForOneProduction(@PathVariable(value = "uuid") String uuid) {
        List<Set<ProductionTime>> productionTimeList = productionService
                .getProductionTimeForOneProduction(uuid);
        return productionTimeList;
    }

    @GetMapping("/allProductionTimes")
    public List<Set<ProductionTime>> getAllProductionTimes() {
        List<Set<ProductionTime>> productionTimeList = productionService
                .getAllProductionTimes();
        return productionTimeList;
    }

    @PostMapping("/start/{uuid}")
    public ResponseEntity<Void> startProductionLine(@PathVariable(value = "uuid") String uuid) {
        productionService.startProduction(uuid);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/stop/{uuid}")
    public ResponseEntity<Void> stopProductionLine(@PathVariable(value = "uuid") String uuid) {
        productionService.stopProduction(uuid);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/test")
    public void test() {
        this.productionService.testSaving();
    }

    @GetMapping("/get-all")
    public List<Production> getAllProductions() {
        return this.productionService.getAllProductions();
    }

}
