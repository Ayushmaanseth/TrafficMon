package com.trafficmon;

import java.time.LocalTime;

public abstract class ZoneBoundaryCrossing {

    private final Vehicle vehicle;
    private final LocalTime time;
    Clock clock = new SystemClock();


    public ZoneBoundaryCrossing(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.time = clock.now();
    }

    public ZoneBoundaryCrossing(Vehicle vehicle,Clock clock){
        this.vehicle = vehicle;
        this.time = clock.now();

    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalTime timestamp() {
        return time;
    }



}

