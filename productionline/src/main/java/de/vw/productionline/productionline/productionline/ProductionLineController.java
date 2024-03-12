package de.vw.productionline.productionline.productionline;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productionline")
public class ProductionLineController {
    
    private final ProductionLineService productionLineService;

    public ProductionLineController(ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
    }

    @GetMapping("/{uuid}")
    public ProductionLine getProductionLineByUUID(@PathVariable(value = "uuid") UUID uuid) {
        return productionLineService.getProductionLineByUUID(uuid);
    }

    @GetMapping()
    public List<ProductionLine> getAllProductionLines() {
        return productionLineService.getAllProductionLines();
    }
}
