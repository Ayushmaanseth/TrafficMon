package com.trafficmon;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.CoreMatchers.containsString;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;


public class CongestionNewSystemTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private AccountsService accountsService = context.mock(AccountsService.class);
    private PenaltiesService penaltiesService = context.mock(PenaltiesService.class);
    private CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(accountsService, penaltiesService);


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

    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }

    @Test
    public void TimeTest() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
            allowing(penaltiesService).issuePenaltyNotice(Vehicle.withRegistration("K083 1LD"), new BigDecimal(22));

        }});
        clock.currentTimeIs(1, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(1, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(5, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(5, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(10, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(10, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(16, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(16, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£22.00"));
        }


    }

    @Test
    public void InvestigationTestForInvalidTimeEntryAndExit() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            allowing(penaltiesService).issuePenaltyNotice(Vehicle.withRegistration("K083 1LD"), new BigDecimal(22));
            allowing(penaltiesService).triggerInvestigationInto(Vehicle.withRegistration("K083 1LD"));
        }});
        clock.currentTimeIs(2, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(2, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(7, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(7, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(1, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(1, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();

    }

    @Test
    public void TestCorrectCharges() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});

        clock.currentTimeIs(2, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(2, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£6.00"));
        }
    }


    @Test
    public void TestCorrectChargesForMoreThanFourHours() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});

        clock.currentTimeIs(2, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(6, 1);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£12.00"));
        }
    }

    @Test
    public void TestOvernightCharges() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});

        clock.currentTimeIs(2, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(2, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(5, 59);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(6, 0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£6.00"));
        }
    }

    @Test
    public void TestOverlappingCharges() throws AccountNotRegisteredException {
        ControllableClock clock = new ControllableClock();

        context.checking(new Expectations() {{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});

        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(14, 30);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(15, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£10.00"));
        }
    }
}


