package de.vw.productionline.productionline.production;

import java.util.List;
import java.util.UUID;

import de.vw.productionline.productionline.productionline.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRepository extends JpaRepository<Production, UUID> {
    List<Production> findAllProductionsByProductionLineUuidAndVehicleModel(
            UUID productionLineUuid, VehicleModel vehicleModel);
    List<Production> findAllProductionsByVehicleModel(VehicleModel vehicleModel);
    List<Production> findAllProductionsByProductionLineUuid(UUID productionLineUuid);

}
