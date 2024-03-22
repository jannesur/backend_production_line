package de.vw.productionline.productionline.production;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.exceptions.ProductionLineAlreadyRunningException;
import de.vw.productionline.productionline.exceptions.ProductionLineIncompleteException;
import de.vw.productionline.productionline.exceptions.ProductionLineNotRunningException;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineService;
import de.vw.productionline.productionline.productionline.SimulationStatus;
import de.vw.productionline.productionline.productionline.Status;
import de.vw.productionline.productionline.productionline.VehicleModel;
import de.vw.productionline.productionline.productiontime.ProductionTime;
import de.vw.productionline.productionline.productiontime.ProductionTimeService;

@Service
public class ProductionService {
    private ProductionRepository productionRepository;
    private ProductionLineService productionLineService;
    private ProductionTimeService productionTimeService;
    private Map<String, Thread> productionThreads = new HashMap<>();
    private long threadCount = 0l;
    private Logger logger = LoggerFactory.getLogger(ProductionService.class);
    private BiConsumer<Production, Set<ProductionTime>> saveProductionAndProductionTimes = this::saveProductionAndProductionTimes;

    public ProductionService(ProductionRepository productionRepository,
            @Lazy ProductionLineService productionLineService,
            ProductionTimeService productionTimeService) {
        this.productionRepository = productionRepository;
        this.productionLineService = productionLineService;
        this.productionTimeService = productionTimeService;
    }

    public void startProduction(String uuid) {
        ProductionLine productionLine = this.productionLineService.getProductionLineById(uuid);
        if (productionLine.getStatus().equals(Status.INCOMPLETE)) {
            throw new ProductionLineIncompleteException();
        }

        if (productionLine.getSimulationStatus().equals(SimulationStatus.RUNNING)) {
            throw new ProductionLineAlreadyRunningException();
        }

        productionLine.setSimulationStatus(SimulationStatus.RUNNING);
        productionLineService.updateProductionLine(productionLine);

        Production production = new Production(productionLine);
        logger.info(String.format("Starting production for production line: %s with UUID %s", productionLine, uuid));
        this.threadCount++;
        String threadName = String.format("Thread %d - %s", this.threadCount,
                productionLine.getVehicleModel());
        Thread productionThread = new Thread(
                new ProductionRunnable(production, threadName, this.threadCount, this.saveProductionAndProductionTimes),
                threadName);
        productionThread.start();
        productionThreads.put(productionLine.getUuid(), productionThread);
    }

    public void stopProduction(String uuid) {
        // If the production line doesn't exist, the service throws an
        // ObjectNotFoundException
        this.productionLineService.getProductionLineById(uuid);
        logger.info(String.format("Ending production for production line UUID: %s", uuid));
        Thread productionThread = this.productionThreads.get(uuid);
        if (productionThread == null) {
            throw new ProductionLineNotRunningException();
        }
        productionThread.interrupt();
        productionThreads.remove(uuid);
    }

    public void saveProductionAndProductionTimes(Production production, Set<ProductionTime> productionTimes) {
        this.productionLineService.updateProductionLine(production.getProductionLine());
        this.productionRepository.save(production);
        saveProductionTimes(productionTimes, production);
    }

    private void saveProductionTimes(Set<ProductionTime> times, Production production) {
        for (ProductionTime productionTime : times) {
            productionTime.setProduction(production);
            this.productionTimeService.saveProductionTime(productionTime);
        }
    }

    public List<Production> getAllProductions() {
        return this.productionRepository.findAll();
    }

    public long getAllProducedCarsFromOneVehicleModel(VehicleModel vehicleModel) {
            return productionRepository.findAllProductionsByVehicleModel(vehicleModel)
                    .stream()
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
    }

    public long getAllProducedCars() {
            return productionRepository.findAll()
                    .stream()
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
    }

    public long getAllProducedCarsFromOneProductionLine(String productionLineUuid) {
            return productionRepository.findAllProductionsByProductionLineUuid(productionLineUuid)
                    .stream()
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
    }

    public long getAllProducedCarsFromOneProductionLineForOneVehicleModel(
            String productionLineUuid,
            VehicleModel vehicleModel) {

            return productionRepository.findAllProductionsByProductionLineUuidAndVehicleModel(
                    productionLineUuid, vehicleModel)
                    .stream()
                    .mapToLong(Production::getNumberProducedCars)
                    .sum();
    }

    public List<Set<ProductionTime>> getAllProductionTimesFromOneProductionLine(String productionLineUuid) {
            return productionRepository.findAllProductionsByProductionLineUuid(productionLineUuid)
                    .stream()
                    .map(Production::getProductionTimes)
                    .toList();
    }

    public List<Set<ProductionTime>> getAllProductionTimesFromOneVehicleModel(VehicleModel vehicleModel) {
            return productionRepository.findAllProductionsByVehicleModel(vehicleModel)
                    .stream()
                    .map(Production::getProductionTimes)
                    .toList();
    }

    public List<Set<ProductionTime>> getProductionTimeForOneProduction(String uuid) {
            return productionRepository.findById(uuid)
                    .stream()
                    .map(Production::getProductionTimes)
                    .toList();
    }

    public List<Set<ProductionTime>> getAllProductionTimes() {
            return productionRepository.findAll()
                    .stream()
                    .map(Production::getProductionTimes)
                    .toList();

    }

}
