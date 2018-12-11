package com.trafficmon;

import java.time.LocalTime;

public class Example {

    public static ControllableClock clock = new ControllableClock();

    public static void main(String[] args) {

        clock.currentTimeIs(14,0);
    CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().build();
    congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
    clock.currentTimeIs(2,0);
    congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
    congestionChargeSystem.calculateCharges();




    }

}
