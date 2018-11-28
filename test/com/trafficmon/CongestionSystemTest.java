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


public class CongestionSystemTest {
    /*
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    public final Vehicle TEST_VEHICLE = Vehicle.withRegistration("K083 1LD");

    public CongestionSystemTest() throws AccountNotRegisteredException {

    }

    private static void delayMinutes(int mins) throws InterruptedException {
        delaySeconds(mins * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }

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

    @Test
    public void CongestionSystemTestOutput() throws AccountNotRegisteredException, InterruptedException {

        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle"));
        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test Vehicle"));
        //delaySeconds(5);
        //congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("J091 4PY"));
        //delaySeconds(15);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"), time);
        //delaySeconds(30);
        //congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("J091 4PY"));
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),is("Penalty notice for: Vehicle [Test Vehicle]\r\n"));
    }

    @Test
    public void AccountServiceCall() throws InterruptedException, AccountNotRegisteredException {
        context.checking(new Expectations(){{
            final Account account = exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        //delaySeconds(5);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), time);
        congestionChargeSystem.calculateCharges();
    }
    @Test
    public void InvalidEntryTriggersInvestigation() throws AccountNotRegisteredException {
        context.checking(new Expectations(){{
            exactly(1).of(penaltiesService).triggerInvestigationInto(Vehicle.withRegistration("K083 1LD"));

        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void RegisteredVehiclesAddition() throws InterruptedException, AccountNotRegisteredException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));
        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        delaySeconds(1);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), time);
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),is("Penalty notice for: Vehicle [K083 1LD]\r\n"));
    }

    @Test
    public void InvalidExitTriggersInvestigation() throws AccountNotRegisteredException {
        context.checking(new Expectations(){{
            exactly(1).of(penaltiesService).triggerInvestigationInto(Vehicle.withRegistration("K083 1LD"));

        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), time);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"), time);
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void InvalidEntryAndExitTriggersInvestigation() throws AccountNotRegisteredException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));

        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"), time);
        congestionChargeSystem.calculateCharges();
    }

*/
}
