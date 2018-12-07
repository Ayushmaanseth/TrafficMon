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
import java.rmi.activation.ActivationException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;


public class CongestionSystemUnitTest {

    private static final Account ACCOUNT = new Account("ABC", Vehicle.withRegistration("Test Vehicle"), new BigDecimal(10));
    private static final Account ACCOUNT2 = new Account("Janos",Vehicle.withRegistration("Test"),new BigDecimal(1000));
    private static final Account ACCOUNT3 = new Account("LOL",Vehicle.withRegistration("K083 1LD"),new BigDecimal(7));
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    public final Vehicle TEST_VEHICLE = Vehicle.withRegistration("K083 1LD");
    private final ControllableClock clock = new ControllableClock();

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
    private CongestionChargeSystem congestionChargeSystem = new builder().setChargeAlgorithm(chargeAlgorithm).setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();




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
        context.checking(new Expectations(){{

            exactly(1).of(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(4)));
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle")); will(returnValue(ACCOUNT2));


        }});
        clock.currentTimeIs(15,0);
        crossings.add(ZoneBoundaryCrossing.createEntryEventWithClock(Vehicle.withRegistration("Test Vehicle"), clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test Vehicle"), clock);
        clock.currentTimeIs(15,30);
        crossings.add(ZoneBoundaryCrossing.createExitEventWithClock(Vehicle.withRegistration("Test Vehicle"), clock));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"), clock);
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void CongestionSystemTestOutput() throws AccountNotRegisteredException {

        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{

            exactly(1).of(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(6)));
            exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle")); will(returnValue(ACCOUNT));
        }});
        clock.currentTimeIs(10,00);
        crossings.add(ZoneBoundaryCrossing.createEntryEventWithClock(Vehicle.withRegistration("Test Vehicle"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test Vehicle"),clock);
        clock.currentTimeIs(11,00);
        crossings.add(ZoneBoundaryCrossing.createExitEventWithClock(Vehicle.withRegistration("Test Vehicle"),clock));
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
        crossings.add(ZoneBoundaryCrossing.createEntryEventWithClock(Vehicle.withRegistration("K083 1LD"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
        clock.currentTimeIs(6,30);
        crossings.add(ZoneBoundaryCrossing.createExitEventWithClock(Vehicle.withRegistration("K083 1LD"),clock));
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
        crossings.add(ZoneBoundaryCrossing.createEntryEventWithClock(Vehicle.withRegistration("K083 1LD"),clock));
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test Vehicle"),clock);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void InvalidEntryExitTest() throws AccountNotRegisteredException, InsufficientCreditException {
        List<ZoneBoundaryCrossing> crossings = new ArrayList<>();
        context.checking(new Expectations(){{

            allowing(accountsService).accountFor(Vehicle.withRegistration("Test")); will(throwException(new AccountNotRegisteredException(Vehicle.withRegistration("Test"))));

            //exactly(1).of(accountsService).accountFor(Vehicle.withRegistration("Test Vehicle")).deduct(new BigDecimal(6)); will(returnValue(ACCOUNT));
            exactly(1).of(penaltiesService).issuePenaltyNotice(Vehicle.withRegistration("Test"),new BigDecimal(6));
            allowing(chargeAlgorithm).calculateChargeForTimeInZone(crossings); will(returnValue(new BigDecimal(6)));
        }});
        clock.currentTimeIs(10,00);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("Test"),clock);
        crossings.add(ZoneBoundaryCrossing.createEntryEventWithClock(Vehicle.withRegistration("Test"),clock));
        clock.currentTimeIs(11,00);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("Test"),clock);
        crossings.add(ZoneBoundaryCrossing.createExitEventWithClock(Vehicle.withRegistration("Test"),clock));
        congestionChargeSystem.calculateCharges();

        //assertThat(outContent.toString(),containsString("Penalty"));

    }


}
