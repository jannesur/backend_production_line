package de.vw.productionline.productionline.production;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;
import de.vw.productionline.productionline.exceptions.ProductionLineNotRunningException;
import de.vw.productionline.productionline.productionline.ProductionLine;
import de.vw.productionline.productionline.productionline.ProductionLineService;
import de.vw.productionline.productionline.productionline.VehicleModel;

@Service
public class ProductionService {
    private ProductionRepository productionRepository;
    private ProductionLineService productionLineService;
    private ProductionTimeService productionTimeService;
    private Map<UUID, Thread> productionThreads = new HashMap<>();
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

    public void startProduction(UUID uuid) {
        ProductionLine productionLine = this.productionLineService.getProductionLineById(uuid);
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

    public void stopProduction(UUID uuid) {
        logger.info(String.format("Ending production for production line UUID: %s", uuid));
        Thread productionThread = this.productionThreads.get(uuid);
        if (productionThread == null) {
            throw new ProductionLineNotRunningException();
        }
        productionThread.interrupt();
        productionThreads.remove(uuid);
    }

    public Production saveProductionAndProductionTimes(Production production, Set<ProductionTime> productionTimes) {
        this.productionLineService.updateProductionLine(production.getProductionLine());
        Production newProduction = this.productionRepository.save(production);
        // for (ProductionTime productionTime : production.getProductionTimes()) {
        // this.productionTimeService.saveProductionTime(productionTime);
        // }
        return newProduction;
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

    public long getAllProducedCarsFromOneProductionLineForOneVehicleModel(UUID productionLineUuid,
            VehicleModel vehicleModel) {
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

    public void testSaving() {

        ProductionLine productionLine = this.productionLineService
                .getProductionLineById(UUID.fromString("51aedaa5-04b2-419f-a481-f7f676bbc0d3"));
        Production production = new Production(productionLine, LocalDateTime.now(), LocalDateTime.now(), 5l, null);
        // production.setProductionTimes(times);
        this.productionRepository.save(production);

        ProductionTime time1 = new ProductionTime(ProductionTimeType.FAILURE, 10l, production);
        ProductionTime time2 = new ProductionTime(ProductionTimeType.MAINTENANCE, 20l, production);
        ProductionTime time3 = new ProductionTime(ProductionTimeType.PRODUCTION, 100l, production);

        Set<ProductionTime> times = new HashSet<>();
        times.add(time1);
        times.add(time2);
        times.add(time3);

        // saveProductionTimes(times);

        for (ProductionTime productionTime : times) {
            this.productionTimeService.saveProductionTime(productionTime);
        }

        // time1.setProduction(production);
        // this.productionTimeService.saveProductionTime(time1);

        // time2.setProduction(production);
        // this.productionTimeService.saveProductionTime(time2);

        // time3.setProduction(production);
        // this.productionTimeService.saveProductionTime(time3);

        // production.setProductionTimes(times);
        // this.productionRepository.save(production);
    }

    private void saveProductionTimes(Set<ProductionTime> times) {
        for (ProductionTime productionTime : times) {
            this.productionTimeService.saveProductionTime(productionTime);
        }
    }

    public List<Production> getAllProductions() {
        return this.productionRepository.findAll();
    }

}
