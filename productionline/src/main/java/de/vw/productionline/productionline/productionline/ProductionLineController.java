package de.vw.productionline.productionline.productionline;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productionline")
public class ProductionLineController {

    private final ProductionLineService productionLineService;

    public ProductionLineController(ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
    }

    @GetMapping("/{uuid}")
    public ProductionLine getProductionLineById(@PathVariable(value = "uuid") UUID uuid) {
        return productionLineService.getProductionLineById(uuid);
    }

    @GetMapping()
    public List<ProductionLine> getAllProductionLines() {
        return productionLineService.getAllProductionLines();
    }

    @PostMapping()
    public ProductionLine createProductionLine(@RequestBody ProductionLine productionLine) {
        return productionLineService.createProductionLine(productionLine);
    }

    @DeleteMapping("/{uuid}")
    public void deleteProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionLineService.deleteProductionLine(uuid);
    }

    // @PutMapping("/{uuid}")
    // public ProductionLine updateProductionLine(@PathVariable(value = "uuid") UUID
    // uuid,
    // @RequestBody ProductionLine productionLine) {
    // return productionLineService.updateProductionLine(uuid, productionLine);
    // }

}
