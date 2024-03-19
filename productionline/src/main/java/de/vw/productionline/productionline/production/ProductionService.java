package de.vw.productionline.productionline.production;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ProductionLineNotRunningException;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineService;

@Service
public class ProductionService {
    private ProductionRepository productionRepository;
    private ProductionLineService productionLineService;
    private ProductionTimeService productionTimeService;
    private Map<UUID, Thread> productionThreads = new HashMap<>();
    private long threadCount = 0l;
    private Logger logger = LoggerFactory.getLogger(ProductionService.class);

    public ProductionService(ProductionRepository productionRepository, ProductionLineService productionLineService,
            ProductionTimeService productionTimeService) {
        this.productionRepository = productionRepository;
        this.productionLineService = productionLineService;
        this.productionTimeService = productionTimeService;
    }

    public void startProduction(UUID uuid) {
        ProductionLine productionLine = this.productionLineService.getProductionLineById(uuid);
        Production production = new Production(productionLine);
        logger.info(String.format("Starting production for production line: %s with UUID %s", productionLine, uuid));
        this.threadCount++;
        String threadName = String.format("Thread %d - %s", this.threadCount,
                productionLine.getVehicleModel());
        Thread productionThread = new Thread(new ProductionRunnable(production, threadName, this.threadCount, this),
                threadName);
        productionThread.start();
        productionThreads.put(productionLine.getUuid(), productionThread);
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

    public Production saveProduction(Production production) {
        this.productionLineService.updateProductionLine(production.getProductionLine());
        for (ProductionTime productionTime : production.getProductionTimes()) {
            this.productionTimeService.saveProductionTime(productionTime);
        }
        return this.productionRepository.save(production);
    }

}
