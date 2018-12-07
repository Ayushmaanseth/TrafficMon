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
import java.time.LocalTime;
import java.util.List;


public class CongestionSystemIntegrationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ControllableClock clock = new ControllableClock();

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private AccountsService accountsService = context.mock(AccountsService.class);
    private PenaltiesService penaltiesService = context.mock(PenaltiesService.class);
    private ChargeAlgorithm chargeAlgorithm = context.mock(ChargeAlgorithm.class);
    private CongestionChargeSystem congestionChargeSystem = new builder().build();


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
    public void TestSystemClock() throws InterruptedException {
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        delaySeconds(1);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            if(LocalTime.now().compareTo(LocalTime.of(14,0)) < 0)
            assertThat(outContent.toString().split(",")[1], containsString("£6.00"));
            else{
                assertThat(outContent.toString().split(",")[1], containsString("£4.00"));
            }
        }
    }

    @Test
    public void TimeTest() {

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
    public void InvestigationTestForInvalidTimeEntryAndExit() {


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

    @Test
    public void MultipleChargeDeduction() throws AccountNotRegisteredException {


        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£6.00"));
        }
    }

    @Test
    public void MultipleVehiclesEnteringAndExitingBeforeTwo() throws AccountNotRegisteredException {


        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"), clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(13,00);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"), clock);
        congestionChargeSystem.calculateCharges();


        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split("\n")[0].split(",")[1], containsString("£6.00"));
            assertThat(outContent.toString().split("\n")[1].split(",")[1], containsString("£6.00"));
        }

    }

    @Test
    public void MultipleVehiclesEnteringAndExitingAfterTwo() throws AccountNotRegisteredException {


        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"), clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(13,0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"), clock);
        clock.currentTimeIs(14,40);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"), clock);
        clock.currentTimeIs(16,0);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"), clock);
        congestionChargeSystem.calculateCharges();


        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split("\n")[0].split(",")[1], containsString("£10.00"));
            assertThat(outContent.toString().split("\n")[1].split(",")[1], containsString("£6.00"));
        }

    }

    @Test
    public void NewAlgorithmTest(){

    }


}


