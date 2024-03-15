package de.vw.productionline.productionline.productionline;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<ProductionLine> getProductionLineById(@PathVariable(value = "uuid") UUID uuid) {
        return ResponseEntity.ok(productionLineService.getProductionLineById(uuid));
    }

    @GetMapping()
    public ResponseEntity<List<ProductionLine>> getAllProductionLines() {
        return ResponseEntity.ok(productionLineService.getAllProductionLines());
    }

    @PostMapping()
    public ResponseEntity<ProductionLine> createProductionLine(@RequestBody ProductionLine productionLine) {
        return new ResponseEntity<>(productionLineService.createProductionLine(productionLine), HttpStatus.CREATED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionLineService.deleteProductionLine(uuid);
        return ResponseEntity.ok(null);
    }

    // @PutMapping("/{uuid}")
    // public ProductionLine updateProductionLine(@PathVariable(value = "uuid") UUID
    // uuid,
    // @RequestBody ProductionLine productionLine) {
    // return productionLineService.updateProductionLine(uuid, productionLine);
    // }

}
