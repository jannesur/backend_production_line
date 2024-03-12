package de.vw.productionline.productionline.productionstep;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductionController {
    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

}
