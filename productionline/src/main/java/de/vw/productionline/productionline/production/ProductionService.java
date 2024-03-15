package de.vw.productionline.productionline.production;

import org.springframework.stereotype.Service;

@Service
public class ProductionService {
    private ProductionRepository productionRepository;

    public ProductionService(ProductionRepository productionRepository) {
        this.productionRepository = productionRepository;
    }

    public Production saveProduction(Production production) {
        return this.productionRepository.save(production);
    }

}
