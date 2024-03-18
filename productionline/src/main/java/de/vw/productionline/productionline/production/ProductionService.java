package de.vw.productionline.productionline.production;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ProductionService {
    private ProductionRepository productionRepository;
    private Map<Production, Thread> productionThreads = new HashMap<>();

    public ProductionService(ProductionRepository productionRepository) {
        this.productionRepository = productionRepository;
    }

    public Production saveProduction(Production production) {
        return this.productionRepository.save(production);
    }

    public void startProduction(Production production) {
        Thread productionThread = new Thread(new ProductionRunnable(production));
        productionThread.start();
        productionThreads.put(production, productionThread);
    }

    public void stopProduction(Production production) {
        Thread productionThread = this.productionThreads.get(production);
        productionThread.interrupt();
    }

}
