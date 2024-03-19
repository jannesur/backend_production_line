package de.vw.productionline.productionline.productionline;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productionline")
@CrossOrigin(origins = "http://localhost:5173/")
public class ProductionLineController {

    private final ProductionLineService productionLineService;
    private Logger logger = LoggerFactory.getLogger(ProductionLineService.class);

    public ProductionLineController(ProductionLineService productionLineService) {
        this.productionLineService = productionLineService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProductionLine> getProductionLineById(@PathVariable(value = "uuid") UUID uuid) {
        logger.info(String.format("Looking for ProductionLine with id %s", uuid));
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

    @PutMapping("/{uuid}")
    public ResponseEntity<ProductionLine> updateProductionLine(@PathVariable UUID uuid,
            @RequestBody ProductionLine updatedProductionLine) {
        return ResponseEntity.ok(productionLineService.updateProductionLine(uuid, updatedProductionLine));
    }

    @PostMapping("/start/{uuid}")
    public ResponseEntity<Void> startProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionLineService.startProduction(uuid);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/stop/{uuid}")
    public ResponseEntity<Void> stopProductionLine(@PathVariable(value = "uuid") UUID uuid) {
        productionLineService.stopProduction(uuid);
        return ResponseEntity.ok(null);
    }

}
