package de.vw.productionline.productionline.productionstep;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductionStepController {
    private final ProductionStepService productionService;

    public ProductionStepController(ProductionStepService productionService) {
        this.productionService = productionService;
    }

}
