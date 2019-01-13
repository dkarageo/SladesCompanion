package com.dkarageo.sladescompanion.vehicles.simulator;

import com.dkarageo.sladescompanion.authorities.Obstacle;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

public class ObstacleGenerator {

    private static String[] obstacleTypes = {
            "oil",
            "ice",
            "road_damage",
            "glass_shards",
            "metallic_shards",
            "corpse",
            "sleeping_grandma_with_walking_stick",
            "other"
    };


    public static Obstacle generateObstacle() {
        Obstacle o = new Obstacle(-1);

        o.setObstacleType(
                obstacleTypes[ThreadLocalRandom.current().nextInt(0, obstacleTypes.length)]
        );

        o.setRequiresService(true);

        Calendar c = GregorianCalendar.getInstance();
        o.setFirstlySpottedOn(c.getTime());
        o.setLastlySpottedOn(c.getTime());

        o.setIsAliveConfidence(
                (float) ThreadLocalRandom.current().nextDouble(0.7, 1)
        );

        return o;
    }
}
