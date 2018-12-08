package com.trafficmon;
import org.junit.Test;

import java.time.temporal.ChronoUnit.*;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static junit.framework.TestCase.*;

import java.time.LocalTime;


public class ControllableClockTest {


    @Test
    public void IfClockTimesSame() {
          ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
         ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 00);

        assertTrue(clock.now().equals(clock2.now()));

    }


    @Test
    public void IfdifferentClockTime() {
        ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
        ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 01);

        assertFalse(clock.now().equals(clock2.now()));
    }

    @Test
    public void comparisonbetweenClockTime() {
        ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
        ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 01);

        assertTrue(clock.now().compareTo(clock2.now()) < 0);

    }

    @Test
    public void MindifferenceBetweenClockTime() {
        ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
        ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 11);

        assertEquals(clock.now().until(clock2.now(),MINUTES),11);

    }

    @Test
    public void gethourClockTime() {
        ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
        ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 11);



        assertEquals(clock.now().getHour(),10);

    }

    @Test
    public void getMinuteClockTime() {
        ControllableClock clock = new ControllableClock();
        clock.currentTimeIs(10, 00);
        ControllableClock clock2 = new ControllableClock();
        clock2.currentTimeIs(10, 11);



        assertEquals(clock.now().getMinute(), 00);

    }


}


