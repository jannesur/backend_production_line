package de.vw.productionline.productionline.robot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RobotRepository extends JpaRepository<Robot, UUID> {
}
