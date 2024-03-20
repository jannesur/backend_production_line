package de.vw.productionline.productionline.productionline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.production.ProductionService;

@Service
public class ProductionLineService {

    private ProductionLineRepository productionLineRepository;
    private ProductionService productionService;
    private Map<UUID, Thread> productionThreads = new HashMap<>();
    private long threadCount = 0l;
    private Logger logger = LoggerFactory.getLogger(ProductionLineService.class);

    public ProductionLineService(ProductionLineRepository productionLineRepository,
            ProductionService productionService) {
        this.productionLineRepository = productionLineRepository;
        this.productionService = productionService;
    }

    public ProductionLine getProductionLineById(String uuid) {
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

    public void deleteProductionLine(String uuid) {
        productionLineRepository.deleteById(uuid);
    }

    public ProductionLine updateProductionLine(String uuid, ProductionLine productionLine) {
        Optional<ProductionLine> optionalProductionLine = productionLineRepository.findById(uuid);
        if (optionalProductionLine.isEmpty()) {
            throw new ObjectNotFoundException("ProductionLine not found");
        }
        ProductionLine existingProductionLine = optionalProductionLine.get();
        existingProductionLine.setName(productionLine.getName());
        existingProductionLine.setSimulationStatus(productionLine.getSimulationStatus());
        existingProductionLine.setStatus(productionLine.getStatus());
        existingProductionLine.setVehicleModel(productionLine.getVehicleModel());
        return productionLineRepository.save(existingProductionLine);
    }

    public ProductionLine updateProductionLine(ProductionLine productionLine) {
        return productionLineRepository.save(productionLine);
    }

}
