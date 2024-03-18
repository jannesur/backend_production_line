package de.vw.productionline.productionline.productionline;

import java.util.*;

import de.vw.productionline.productionline.exceptions.ProductionLineIncompleteException;
import de.vw.productionline.productionline.exceptions.ProductionLineNotRunningException;
import de.vw.productionline.productionline.production.Production;
import de.vw.productionline.productionline.production.ProductionRunnable;
import de.vw.productionline.productionline.production.ProductionService;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.productionstep.ProductionStatus;
import de.vw.productionline.productionline.productionstep.ProductionStep;

@Service
public class ProductionLineService {

    private ProductionLineRepository productionLineRepository;
    private Map<Production, Thread> productionThreads = new HashMap<>();

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

    public ProductionLine updateProductionLine(UUID uuid, ProductionLine productionLine) {
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

    public void startProduction(UUID uuid) {
        Production production = new Production(getProductionLineById(uuid));
        Thread productionThread = new Thread(new ProductionRunnable(production));
        productionThread.start();
        productionThreads.put(production, productionThread);
    }

    public void stopProduction(UUID uuid) {
        Production production = new Production(getProductionLineById(uuid));
        Thread productionThread = this.productionThreads.get(production);
        if (productionThread == null) {
            throw new ProductionLineNotRunningException();
        }
        productionThread.interrupt();
    }

}
