package com.trafficmon;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.trafficmon.Builder.*;
import static com.trafficmon.ZoneBoundaryCrossing.*;


public class CongestionSystemUnitTest {

    private static final Account ACCOUNT = new Account("ABC", Vehicle.withRegistration("Test Vehicle"), new BigDecimal(10));
    private static final Account ACCOUNT2 = new Account("Janos",Vehicle.withRegistration("Test"),new BigDecimal(1));
    private static final Account ACCOUNT3 = new Account("LOL",Vehicle.withRegistration("K083 1LD"),new BigDecimal(7));
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    public final Vehicle TEST_VEHICLE = Vehicle.withRegistration("K083 1LD");
    private final ControllableClock clock = new ControllableClock();
    private final Vehicle testVehicle = Vehicle.withRegistration("Test");

    public CongestionSystemUnitTest() {

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
    private ChargeAlgorithm chargeAlgorithm = context.mock(ChargeAlgorithm.class);
    private CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();




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
    public void ChargesFourPoundsForEnteringAfterTwoPM() throws AccountNotRegisteredException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                                                            setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{
            exactly(1).of(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(4)));
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle")); will(returnValue(ACCOUNT));

        }});
        clock.currentTimeIs(15,0);
        crossings.add(createEntryEvent(Vehicle.withRegistration("Test Vehicle"), clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test Vehicle"), clock);
        clock.currentTimeIs(15,30);
        crossings.add(createExitEvent(Vehicle.withRegistration("Test Vehicle"), clock));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"), clock);
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void ChargesSixPoundsForEnteringBeforeTwoPM() throws AccountNotRegisteredException {

        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{

            exactly(1).of(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(6)));
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle")); will(returnValue(ACCOUNT));
        }});
        clock.currentTimeIs(10,00);
        crossings.add(createEntryEvent(Vehicle.withRegistration("Test Vehicle"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test Vehicle"),clock);
        clock.currentTimeIs(11,00);
        crossings.add(createExitEvent(Vehicle.withRegistration("Test Vehicle"),clock));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"),clock);
        congestionChargeSystem.calculateCharges();

    }

    @Test
    public void AccountServiceCall() throws AccountNotRegisteredException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{
            allowing(penaltiesService).issuePenaltyNotice(Vehicle.withRegistration("K083 1LD"),new BigDecimal(6));

                allowing(chargeAlgorithm).calculateChargeForTimeInZone(crossings);
                will(returnValue(new BigDecimal(6)));
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));will(returnValue(ACCOUNT));
        }});
        clock.currentTimeIs(6,0);
        crossings.add(createEntryEvent(Vehicle.withRegistration("K083 1LD"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
        clock.currentTimeIs(6,30);
        crossings.add(createExitEvent(Vehicle.withRegistration("K083 1LD"),clock));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.calculateCharges();
    }
    @Test
    public void InvalidEntryTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(penaltiesService).triggerInvestigationInto(Vehicle.withRegistration("K083 1LD"));

        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.calculateCharges();
    }




    @Test
    public void InvalidExitTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(penaltiesService).triggerInvestigationInto(Vehicle.withRegistration("K083 1LD"));

        }});
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.calculateCharges();
    }

    //If you haven't entered a zone then you can't exit it
    @Test
    public void IfNotEnteredThenCannotExist() throws AccountNotRegisteredException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("K083 1LD"));will(returnValue(ACCOUNT3));
            allowing(chargeAlgorithm).calculateChargeForTimeInZone(crossings);will(returnValue(new BigDecimal(4)));

        }});
        clock.currentTimeIs(14,38);
        crossings.add(createEntryEvent(Vehicle.withRegistration("K083 1LD"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"),clock);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void AccountNotRegisteredExceptionTest() throws AccountNotRegisteredException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{

            allowing(accountsService).accountFor(testVehicle); will(throwException(
                                            new AccountNotRegisteredException(testVehicle)));

            exactly(1).of(penaltiesService).issuePenaltyNotice(testVehicle,new BigDecimal(6));
            allowing(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(6)));
        }});
        clock.currentTimeIs(10,00);
        congestionChargeSystem.vehicleEnteringZone(testVehicle,clock);
        crossings.add(createEntryEvent(testVehicle,clock));
        clock.currentTimeIs(11,00);
        congestionChargeSystem.vehicleLeavingZone(testVehicle,clock);
        crossings.add(createExitEvent(testVehicle,clock));
        congestionChargeSystem.calculateCharges();


    }

    @Test
    public void InsufficientCreditExceptionTest() throws AccountNotRegisteredException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        final BigDecimal credit = new BigDecimal(6);
        context.checking(new Expectations(){{

                exactly(1).of(accountsService).accountFor(testVehicle);
                will(returnValue(ACCOUNT2));
                exactly(1).of(penaltiesService).issuePenaltyNotice(testVehicle, credit);
                allowing(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(credit));
        }});
        clock.currentTimeIs(10,00);
        congestionChargeSystem.vehicleEnteringZone(testVehicle,clock);
        crossings.add(createEntryEvent(testVehicle,clock));
        clock.currentTimeIs(11,00);
        congestionChargeSystem.vehicleLeavingZone(testVehicle,clock);
        crossings.add(createExitEvent(testVehicle,clock));
        congestionChargeSystem.calculateCharges();


    }


}
