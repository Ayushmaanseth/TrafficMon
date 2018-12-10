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

import static com.trafficmon.Builder.createBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class PredictableAlgorithmTest {

    private static final Account ACCOUNT3 = new Account("LOL",Vehicle.withRegistration("K083 1LD"),new BigDecimal(7));
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ControllableClock clock = new ControllableClock();
    private final Vehicle testVehicle = Vehicle.withRegistration("Test");
    private final Vehicle testVehicle2 = Vehicle.withRegistration("Test Vehicle");

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

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private AccountsService accountsService = context.mock(AccountsService.class);
    private PenaltiesService penaltiesService = context.mock(PenaltiesService.class);
    private ChargeAlgorithm chargeAlgorithm = new PredictableChargeAlgorithm();

    @Test
    public void ChargesFourPoundsForEnteringAfterTwoPM() throws AccountNotRegisteredException {
        final Account ACCOUNT = new Account("ABC", Vehicle.withRegistration("Test Vehicle"), new BigDecimal(10));
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(testVehicle2); will(returnValue(ACCOUNT));

        }});
        clock.currentTimeIs(15,0);
        congestionChargeSystem.vehicleEnteringZone(testVehicle2, clock);
        clock.currentTimeIs(15,30);
        congestionChargeSystem.vehicleLeavingZone(testVehicle2, clock);
        congestionChargeSystem.calculateCharges();

        assertThat(outContent.toString().split(",")[1], containsString("£4.00"));
    }

    @Test
    public void ChargesSixPoundsForEnteringBeforeTwoPM() throws AccountNotRegisteredException {
        final Account ACCOUNT = new Account("ABC", Vehicle.withRegistration("Test Vehicle"), new BigDecimal(10));
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{

            exactly(1).of(accountsService).accountFor(testVehicle2); will(returnValue(ACCOUNT));
        }});
        clock.currentTimeIs(10,00);
        congestionChargeSystem.vehicleEnteringZone(testVehicle2,clock);
        clock.currentTimeIs(11,00);
        congestionChargeSystem.vehicleLeavingZone(testVehicle2,clock);
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(), containsString("Charge made to account of ABC, £6.00 deducted, balance: £4.00"));
    }

    @Test
    public void AccountUnregisteredThrowsExceptionChargedSixPounds() throws AccountNotRegisteredException {
        //final Account ACCOUNT2 = new Account("Janos",testVehicle,new BigDecimal(1));
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(testVehicle); will(throwException(new AccountNotRegisteredException(testVehicle)));
            exactly(1).of(penaltiesService).issuePenaltyNotice(testVehicle,new BigDecimal(6));
        }});

        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(testVehicle, clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(testVehicle,clock);
        congestionChargeSystem.calculateCharges();
    }
    @Test
    public void AccountUnregisteredThrowsExceptionChargedFourPounds() throws AccountNotRegisteredException {
        //final Account ACCOUNT2 = new Account("Janos",testVehicle,new BigDecimal(1));
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(testVehicle); will(throwException(
                                                                            new AccountNotRegisteredException(testVehicle)));
            exactly(1).of(penaltiesService).issuePenaltyNotice(testVehicle,new BigDecimal(4));
        }});

        clock.currentTimeIs(15, 0);
        congestionChargeSystem.vehicleEnteringZone(testVehicle, clock);
        clock.currentTimeIs(15, 30);
        congestionChargeSystem.vehicleLeavingZone(testVehicle,clock);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void InsufficientCreditExceptionSixPoundsCharged() throws AccountNotRegisteredException {
        final Account ACCOUNT2 = new Account("Janos",testVehicle,new BigDecimal(1));
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(chargeAlgorithm).
                setAccountsService(accountsService).setPenaltiesService(penaltiesService).build();
        context.checking(new Expectations(){{
            exactly(1).of(accountsService).accountFor(testVehicle); will(returnValue(ACCOUNT2));
            exactly(1).of(penaltiesService).issuePenaltyNotice(testVehicle,new BigDecimal(4));
        }});
        clock.currentTimeIs(15, 0);
        congestionChargeSystem.vehicleEnteringZone(testVehicle, clock);
        clock.currentTimeIs(15, 30);
        congestionChargeSystem.vehicleLeavingZone(testVehicle,clock);
        congestionChargeSystem.calculateCharges();
    }


}
