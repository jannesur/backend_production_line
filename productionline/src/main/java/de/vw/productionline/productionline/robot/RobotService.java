package de.vw.productionline.productionline.robot;

import org.springframework.stereotype.Service;

@Service
public class RobotService {
    private RobotRepository robotRepository;

    public RobotService(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

}
