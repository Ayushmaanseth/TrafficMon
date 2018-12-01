package com.trafficmon;

public abstract class ZoneBoundaryCrossing {

    private final Vehicle vehicle;
    private final double time;


    public ZoneBoundaryCrossing(Vehicle vehicle, double time) {
        this.vehicle = vehicle;
        this.time = time;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double timestamp() {
        return time;
    }
}
