package de.vw.productionline.productionline.production;

import java.util.List;

import de.vw.productionline.productionline.productionline.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRepository extends JpaRepository<Production, String> {
    List<Production> findAllProductionsByProductionLineUuidAndVehicleModel(
            String productionLineUuid, VehicleModel vehicleModel);

    List<Production> findAllProductionsByVehicleModel(VehicleModel vehicleModel);

    List<Production> findAllProductionsByProductionLineUuid(String productionLineUuid);

}
