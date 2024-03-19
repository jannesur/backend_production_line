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
import de.vw.productionline.productionline.exceptions.ProductionLineNotRunningException;
import de.vw.productionline.productionline.production.Production;
import de.vw.productionline.productionline.production.ProductionRunnable;

@Service
public class ProductionLineService {

    private ProductionLineRepository productionLineRepository;
    private Map<UUID, Thread> productionThreads = new HashMap<>();
    private long threadCount = 0l;
    private Logger logger = LoggerFactory.getLogger(ProductionLineService.class);

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
        ProductionLine productionLine = getProductionLineById(uuid);
        logger.info(String.format("Starting production for production line: %s with UUID %s", productionLine, uuid));
        Production production = new Production(productionLine);
        this.threadCount++;
        String threadName = String.format("Thread %d - %s", this.threadCount,
                productionLine.getVehicleModel());
        Thread productionThread = new Thread(new ProductionRunnable(production, threadName, this.threadCount),
                threadName);
        productionThread.start();
        productionThreads.put(uuid, productionThread);
    }

    public void stopProduction(UUID uuid) {
        logger.info(String.format("Ending production for production line UUID: %s", uuid));
        Thread productionThread = this.productionThreads.get(uuid);
        if (productionThread == null) {
            throw new ProductionLineNotRunningException();
        }
        productionThread.interrupt();
        productionThreads.remove(uuid);
    }

}
