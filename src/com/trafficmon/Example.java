package com.trafficmon;

public class Example {
    public static void main(String[] args) throws Exception {

        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),2.0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),2.5);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),6.0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),6.5);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),10.0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),10.5);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),11.0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),11.5);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),12.5);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),13.0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),14.5);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),15.0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"),16.5);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"),17.0);

        congestionChargeSystem.calculateCharges();

    }
    private static void delayMinutes(int mins) throws InterruptedException {
        delaySeconds(mins * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }
}
