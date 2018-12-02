package com.trafficmon;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class CongestionNewSystemTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private AccountsService accountsService = context.mock(AccountsService.class);
    private PenaltiesService penaltiesService = context.mock(PenaltiesService.class);
    private CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(accountsService,penaltiesService);


    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void TimeTest() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});
        clock.currentTimeIs(2,0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
        clock.currentTimeIs(2,30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.calculateCharges();

    }

}


