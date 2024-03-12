package de.vw.productionline.productionline.productionline;

import java.util.List;
import java.util.Optional;

import java.util.UUID;


import org.springframework.stereotype.Service;

@Service
public class ProductionLineService {
    
    private ProductionLineRepository productionLineRepository;

    public ProductionLineService(ProductionLineRepository productionLineRepository) {
        this.productionLineRepository = productionLineRepository;
    }

    public ProductionLine getProductionLineByUUID(UUID uuid) {
        Optional<ProductionLine> productionLine = productionLineRepository.findByUUID(uuid);
        return productionLine.orElse(null);
    }

    public List<ProductionLine> getAllProductionLines() {
        return productionLineRepository.findAll();
    }
    

    
}
