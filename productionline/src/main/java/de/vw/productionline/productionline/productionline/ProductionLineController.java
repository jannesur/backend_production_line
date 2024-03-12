package de.vw.productionline.productionline.productionline;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductionLineController {
    
    private final ProductionLineService productionLineService;

    public ProductionLineController(ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
    }
}
