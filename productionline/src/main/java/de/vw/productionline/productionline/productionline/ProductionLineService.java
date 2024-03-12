package de.vw.productionline.productionline.productionline;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;

@Service
public class ProductionLineService {

    private ProductionLineRepository productionLineRepository;

    public ProductionLineService(ProductionLineRepository productionLineRepository) {
        this.productionLineRepository = productionLineRepository;
    }

    public ProductionLine getProductionLineById(UUID uuid) {
        Optional<ProductionLine> productionLine = productionLineRepository.findById(uuid);
        if (productionLine.isEmpty()) {
            throw new ObjectNotFoundException("ProductionLine not found");
        }
        return productionLine.get();
    }

    public List<ProductionLine> getAllProductionLines() {
        return productionLineRepository.findAll();
    }

    public ProductionLine createProductionLine(ProductionLine productionLine) {
        return productionLineRepository.save(productionLine);
    }

    public void deleteProductionLine(UUID uuid) {
        productionLineRepository.deleteById(uuid);
    }

}
