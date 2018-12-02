package com.trafficmon;

import java.time.LocalTime;

public class ControllableClock implements Clock {
    private LocalTime now;

    @Override
    public LocalTime now() {
        return now;
    }
    public void currentTimeIs(int hour,int min){
        now = LocalTime.of(hour, min);
    }
}
