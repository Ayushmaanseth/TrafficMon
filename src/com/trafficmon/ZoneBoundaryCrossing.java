package com.trafficmon;

import java.time.LocalTime;
import java.util.Objects;

public class ZoneBoundaryCrossing {

        private final Vehicle vehicle;
        private final LocalTime time;
        static Clock clock = new SystemClock();
        private final String typeofEvent;

    public static ZoneBoundaryCrossing createExitEvent(Vehicle vehicle) {
        return new ZoneBoundaryCrossing(vehicle,"Exit");
    }

    public static ZoneBoundaryCrossing createExitEventWithClock(Vehicle vehicle, Clock clock) {
        return new ZoneBoundaryCrossing(vehicle, clock,"Exit");
    }


    public String getTypeofEvent() {
        return typeofEvent;
    }

    public ZoneBoundaryCrossing(Vehicle vehicle, String typeofEvent) {
            this.vehicle = vehicle;
            this.time = clock.now();
            this.typeofEvent = typeofEvent;
        }

        public ZoneBoundaryCrossing(Vehicle vehicle,Clock clock,String typeofEvent){
            this.vehicle = vehicle;
            this.time = clock.now();
            this.typeofEvent = typeofEvent;
        }

    public static ZoneBoundaryCrossing createEntryEvent(Vehicle vehicleRegistration) {
        return new ZoneBoundaryCrossing(vehicleRegistration,clock,"Entry");
    }

    public static ZoneBoundaryCrossing createEntryEventWithClock(Vehicle vehicleRegistration, Clock clock) {
        return new ZoneBoundaryCrossing(vehicleRegistration, clock,"Entry");
    }

    public Vehicle getVehicle() {
            return vehicle;
        }

        public LocalTime timestamp() {
            return time;
        }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneBoundaryCrossing that = (ZoneBoundaryCrossing) o;
        return Objects.equals(vehicle, that.vehicle) &&
                Objects.equals(time, that.time) &&
                Objects.equals(typeofEvent, that.typeofEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicle, time, typeofEvent);
    }
}

