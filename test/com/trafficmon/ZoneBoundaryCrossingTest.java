package com.trafficmon;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class ZoneBoundaryCrossingTest
{
    private final ControllableClock clock = new ControllableClock();

    @Test
    public void TwoZoneBoundaryInstanceEqual(){
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();

        clock.currentTimeIs(10,00);
        crossings.add(ZoneBoundaryCrossing.createEntryEvent(Vehicle.withRegistration("Test Vehicle"), clock));

        clock.currentTimeIs(10,00);
        crossings.add(ZoneBoundaryCrossing.createEntryEvent(Vehicle.withRegistration("Test Vehicle"), clock));
        assertTrue(crossings.get(0).equals(crossings.get(1)));
    }

    @Test
    public void SameHashCodeForTwoTwoZoneBoundaryCrossing() {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();

        clock.currentTimeIs(10,00);
        crossings.add(ZoneBoundaryCrossing.createEntryEvent(Vehicle.withRegistration("Test Vehicle"), clock));

        clock.currentTimeIs(10,00);
        crossings.add(ZoneBoundaryCrossing.createEntryEvent(Vehicle.withRegistration("Test Vehicle"), clock));

        assertEquals(crossings.get(0).hashCode(),crossings.get(1).hashCode());

    }
}
