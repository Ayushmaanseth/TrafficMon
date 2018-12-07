package com.trafficmon;

public class Example {



    public static void main(String[] args) throws Exception {

        //ControllableClock clock = new ControllableClock();
        CongestionChargeSystem congestionChargeSystem = new builder().build();


        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("lol"));
        delaySeconds(1);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("lol"));
        //congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
        congestionChargeSystem.calculateCharges();

    }
    private static void delayMinutes(int min) throws InterruptedException {
        delaySeconds(min * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);

    }

}
