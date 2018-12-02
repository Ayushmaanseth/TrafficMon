package com.trafficmon;

import java.time.LocalTime;

public class ExitEvent extends ZoneBoundaryCrossing {
    public ExitEvent(Vehicle vehicle) {

        super(vehicle);
    }

    public ExitEvent(Vehicle vehicle,Clock clock){
        super(vehicle,clock);
    }
}
