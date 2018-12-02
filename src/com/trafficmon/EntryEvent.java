package com.trafficmon;

import java.time.LocalTime;

public class EntryEvent extends ZoneBoundaryCrossing {

    public EntryEvent(Vehicle vehicleRegistration) {
        super(vehicleRegistration);
    }

    public EntryEvent(Vehicle vehicleRegistration,Clock clock){
        super(vehicleRegistration,clock);
    }
}
