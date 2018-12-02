package com.trafficmon;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

public class Example {



    public static void main(String[] args) throws Exception {

        ControllableClock clock = new ControllableClock();
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        clock.currentTimeIs(2,00);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(2,30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(3,00);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(3,30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(5,00);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(5,30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(14,00);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),clock);
        clock.currentTimeIs(15,00);

        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),clock);
        congestionChargeSystem.calculateCharges();

    }
    private static void delayMinutes(int min) throws InterruptedException {
        delaySeconds(min * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }
}
