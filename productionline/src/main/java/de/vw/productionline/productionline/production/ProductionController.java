package de.vw.productionline.productionline.production;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/production")
public class ProductionController {
    private ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
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

}
