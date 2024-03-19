package de.vw.productionline.productionline.production;

import de.vw.productionline.productionline.productionline.VehicleModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
}
