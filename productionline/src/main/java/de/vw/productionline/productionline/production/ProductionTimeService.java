package de.vw.productionline.productionline.production;

import org.springframework.stereotype.Service;

@Service
public class ProductionTimeService {

    private ProductionTimeRepository productionTimeRepository;

    public ProductionTimeService(ProductionTimeRepository productionTimeRepository) {
        this.productionTimeRepository = productionTimeRepository;
    }

    public ProductionTime saveProductionTime(ProductionTime productionTime) {
        return this.productionTimeRepository.save(productionTime);
    }
}
