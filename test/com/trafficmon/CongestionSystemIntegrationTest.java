package com.trafficmon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class CongestionSystemIntegrationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ControllableClock clock = new ControllableClock();

    private CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().build();


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
    public void SingleVehicleEnteringAndExitingMoreThanOnceaaq() throws AccountNotRegisteredException {


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
    public void SingleVehicleTest() throws AccountNotRegisteredException {


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
    public void AnyOtherAlgorithmWorksFineWithTheSystem(){
        CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
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
        CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K083 1LD"),clock);
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),is("Mismatched entries/exits. Triggering investigation into vehicle: Vehicle [K083 1LD]\r\n"));

    }

    @Test
    public void ExpectPenaltyNoticeWithAnyOtherAlgorithm(){
        CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        clock.currentTimeIs(11, 0);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("abc"), clock);
        clock.currentTimeIs(11, 30);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("abc"),clock);

        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString(),is("Penalty notice for: Vehicle [abc]\r\n"));

    }


    @Test
    public void TestSystemClockWithOneSecondDelay() throws InterruptedException {
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
        CongestionChargeSystem congestionChargeSystem = Builder.createBuilder().setChargeAlgorithm(new OldAlgorithm()).build();
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K083 1LD"));
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("test"));
        congestionChargeSystem.calculateCharges();
        assertThat(outContent.toString().split(",")[1], containsString("£0.00"));
    }

}


