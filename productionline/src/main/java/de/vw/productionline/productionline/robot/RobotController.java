package de.vw.productionline.productionline.robot;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://localhost:5173/")
public class RobotController {
    private RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @GetMapping()
    public ResponseEntity<List<Robot>> getAllRobots() {
        return ResponseEntity.ok(robotService.getAllRobots());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Robot> getRobotById(@PathVariable(value = "uuid") UUID uuid) {
        return ResponseEntity.ok(robotService.getRobotById(uuid));
    }

    @PostMapping()
    public ResponseEntity<Robot> createRobot(@RequestBody Robot robot) {
        return new ResponseEntity<>(robotService.createRobot(robot), HttpStatus.CREATED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteRobot(@PathVariable(value = "uuid") UUID uuid) {
        robotService.deleteRobot(uuid);
        return new ResponseEntity<>(null);
    }

    @GetMapping("/without-robot")
    public ResponseEntity<List<Robot>> getRobotsWithoutProductionLine() {
        return ResponseEntity.ok(robotService.getAllRobotsNotInProductionLine());
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Robot> updateRobot(@PathVariable UUID uuid, @RequestBody Robot updatedRobot) {
        return ResponseEntity.ok(robotService.updateRobot(uuid, updatedRobot));
    }
}
