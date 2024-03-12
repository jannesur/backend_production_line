package de.vw.productionline.productionline.productionstep;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineRepository;

@Service
public class ProductionStepService {

    private ProductionStepRepository productionStepRepository;

    private ProductionLineRepository productionLineRepository;

    public ProductionStepService(ProductionStepRepository productionStepRepository) {
        this.productionStepRepository = productionStepRepository;
    }

    public List<ProductionStep> getAllProductionStepsInOrderByProductionLineId(UUID productionLineId) {
        Optional<ProductionLine> productionLine = productionLineRepository.findById(productionLineId);
        if (productionLine.isPresent()) {
            return productionLine.get().getProductionSteps().stream()
                    .sorted(Comparator.comparing(ProductionStep::getStepNumber))
                    .toList();
        }
        return List.of();
    }

}
