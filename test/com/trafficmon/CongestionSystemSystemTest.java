package com.trafficmon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;

import static com.trafficmon.Builder.createBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;


public class CongestionSystemSystemTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ControllableClock clock = new ControllableClock();


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
    public void MultipleEntriesAndExitsTest() {
        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
        assertThat(outContent.toString(), containsString("Mismatched entries/exits. Triggering investigation into vehicle: Vehicle [K083 1LD]"));
    }

    @Test
    public void TestCorrectCharges() {


        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void TestCorrectChargesForMoreThanFourHours() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void TestOvernightCharges() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void SingleVehicleEnteringAndExitingMoreThanOnce() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void SingleVehicleTest() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void MultipleVehiclesEnteringAndExitingBeforeTwo() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void MultipleVehiclesEnteringAndExitingAfterTwo() {

        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void AnyOtherAlgorithmWorksFineWithTheSystem(){
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.calculateCharges();
        if (!outContent.toString().contains("Penalty")) {
            assertThat(outContent.toString().split(",")[1], containsString("£1.50"));
        }
    }

    @Test
    public void ExpectInvestigationTriggerWithAnyOtherAlgorithm(){
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),containsString("Mismatched entries/exits. Triggering investigation into vehicle: Vehicle [K083 1LD]"));

    }

    @Test
    public void ExpectPenaltyNoticeWithAnyOtherAlgorithm(){
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("abc"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("abc"),clock);

        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),containsString("Penalty notice for: Vehicle [abc]"));

    }


    @Test
    public void TestSystemClockWithOneSecondDelay() throws InterruptedException {
        CongestionChargeSystem congestionChargeSystem = createBuilder().build();
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
    public void TestWithSystemClockForNoPreviouslyRegisteredVehicle() {
        CongestionChargeSystem congestionChargeSystem = createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("test"));
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString().split(",")[1], containsString("£0.00"));
    }




}


