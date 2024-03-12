package de.vw.productionline.productionline.robot;


import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotController {

    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }
}
