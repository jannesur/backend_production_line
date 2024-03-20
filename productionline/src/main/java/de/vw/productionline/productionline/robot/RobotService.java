package de.vw.productionline.productionline.robot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

import de.vw.productionline.productionline.exceptions.ObjectNotFoundException;

@Service
public class RobotService {
    private RobotRepository robotRepository;

    public RobotService(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }

    public Robot getRobotById(String uuid) {
        Optional<Robot> robot = robotRepository.findById(uuid);
        if (robot.isEmpty()) {
            throw new ObjectNotFoundException("Robot not found");
        }
        return robot.get();
    }

    public Robot createRobot(Robot robot) {
        return robotRepository.save(robot);
    }

    public void deleteRobot(String uuid) {
        robotRepository.deleteById(uuid);
    }

    public List<Robot> getAllRobotsNotInProductionLine() {
        List<Robot> robots = this.robotRepository.findAllByProductionLineIsNull();
        return robots;
    }

    public Robot updateRobot(String uuid, Robot robot) {
        Optional<Robot> optionalRobot = robotRepository.findById(uuid);
        if (optionalRobot.isEmpty()) {
            throw new ObjectNotFoundException("Robot not found");
        }
        Robot existingRobot = optionalRobot.get();
        existingRobot.setName(robot.getName());
        existingRobot.setDurationInMinutes(robot.getDurationInMinutes());
        existingRobot.setFailureProbability(robot.getFailureProbability());
        existingRobot.setTimeToRecovery(robot.getTimeToRecovery());
        existingRobot.setProductionLine(robot.getProductionLine());
        existingRobot.setMaintenanceCycleInMinutes(robot.getMaintenanceCycleInMinutes());
        existingRobot.setMaintenanceTimeInMinutes(robot.getMaintenanceTimeInMinutes());
        return robotRepository.save(existingRobot);
    }
}
