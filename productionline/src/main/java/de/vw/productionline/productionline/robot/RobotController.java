package de.vw.productionline.productionline.robot;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/robots")
public class RobotController {
    private RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @GetMapping()
    public List<Robot> getAllRobots() {
        return robotService.getAllRobots();
    }

    @GetMapping("/{uuid}")
    public Robot getRobotById(@PathVariable(value = "uuid") UUID uuid) {
        return robotService.getRobotById(uuid);
    }

    @PostMapping()
    public Robot createRobot(@RequestBody Robot robot) {
        return robotService.createRobot(robot);
    }

    @DeleteMapping("/{uuid}")
    public void deleteRobot(@PathVariable(value = "uuid") UUID uuid) {
        robotService.deleteRobot(uuid);
    }

    @GetMapping("without")
    public List<Robot> getRobotsWithoutProductionLine() {
        return robotService.getAllRobotsNotInProductionLine();
    }
    
    @PutMapping("/{uuid}")
    public Robot updateRobot(@PathVariable UUID uuid, @RequestBody Robot updatedRobot) {
        return robotService.updateRobot(uuid, updatedRobot);
    }
}
