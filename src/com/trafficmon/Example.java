package com.trafficmon;

import java.time.LocalTime;

public class Example {

    public static ControllableClock clock = new ControllableClock();

    public static void main(String[] args) {

    CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().build();
    congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
    congestionChargeSystem.calculateCharges();




    }

}
