package com.trafficmon;

public class Example {
    public static void main(String[] args) throws Exception {

        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),13);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),15);
        //congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),15);
        //congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),16);
        congestionChargeSystem.calculateCharges();

    }
    private static void delayMinutes(int mins) throws InterruptedException {
        delaySeconds(mins * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }
}
