package de.vw.productionline.productionline.production;

import java.util.*;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.productionline.VehicleModel;
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

    public long getAllProducedCarsFromOneVehicleModel(VehicleModel vehicleModel) {
        try {
            return productionRepository.findAll()
                    .stream()
                    .filter(production -> production.getVehicleModel().equals(vehicleModel))
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("No produced cars found for : " + vehicleModel);
        }
    }

    public long getAllProducedCars() {
        try {
            return productionRepository.findAll()
                    .stream()
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("No produced cars found");
        }
    }

    public long getAllProducedCarsFromOneProductionLine(UUID productionLineUuid) {
        try {
            return productionRepository.findAll()
                    .stream()
                    .filter(production -> production.getProductionLineUuid().equals(productionLineUuid))
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("No produced cars found for :" + productionLineUuid);
        }
    }

    public long getAllProducedCarsFromOneProductionLineForOneVehicleModel(UUID productionLineUuid, VehicleModel vehicleModel) {
        try {
            return productionRepository.findAll()
                    .stream()
                    .filter(production -> production.getProductionLineUuid().equals(productionLineUuid))
                    .filter(production -> production.getVehicleModel().equals(vehicleModel))
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("No produced cars found for :" + productionLineUuid + " " + vehicleModel);
        }
    }


}
