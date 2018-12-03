package com.trafficmon;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class CongestionChargeSystem {

    public static final BigDecimal CHARGE_RATE_POUNDS_PER_MINUTE = new BigDecimal(3000);

    private final List<ZoneBoundaryCrossing> eventLog = new ArrayList<>();

    private final AccountsService accountsService;
    private final PenaltiesService penaltiesService;

    public CongestionChargeSystem(){
        this.accountsService = RegisteredCustomerAccountsService.getInstance();
        this.penaltiesService = OperationsTeam.getInstance();
    }
    public CongestionChargeSystem(AccountsService accountsService,PenaltiesService penaltiesService){
        this.penaltiesService = penaltiesService;
        this.accountsService = accountsService;
    }

    public void vehicleEnteringZone(Vehicle vehicle) {
        eventLog.add(new EntryEvent(vehicle));
    }
    public void vehicleEnteringZone(Vehicle vehicle,Clock clock) {
        eventLog.add(new EntryEvent(vehicle,clock));
    }

    public void vehicleLeavingZone(Vehicle vehicle) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(new ExitEvent(vehicle));
    }

    public void vehicleLeavingZone(Vehicle vehicle,Clock clock) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(new ExitEvent(vehicle,clock));
    }



    public void calculateCharges() {

        Map<Vehicle, List<ZoneBoundaryCrossing>> crossingsByVehicle = new HashMap<Vehicle, List<ZoneBoundaryCrossing>>();

        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (!crossingsByVehicle.containsKey(crossing.getVehicle())) {
                crossingsByVehicle.put(crossing.getVehicle(), new ArrayList<ZoneBoundaryCrossing>());
            }
            crossingsByVehicle.get(crossing.getVehicle()).add(crossing);
        }

        for (Map.Entry<Vehicle, List<ZoneBoundaryCrossing>> vehicleCrossings : crossingsByVehicle.entrySet()) {
            Vehicle vehicle = vehicleCrossings.getKey();
            List<ZoneBoundaryCrossing> crossings = vehicleCrossings.getValue();

            if (!checkOrderingOf(crossings)) {
                OperationsTeam.getInstance().triggerInvestigationInto(vehicle);
                penaltiesService.triggerInvestigationInto(vehicle);
            } else {

                BigDecimal charge = calculateChargeForTimeInZone(crossings);

                try {
                    accountsService.accountFor(vehicle);
                    RegisteredCustomerAccountsService.getInstance().accountFor(vehicle).deduct(charge);
                } catch (InsufficientCreditException | AccountNotRegisteredException ice) {
                    OperationsTeam.getInstance().issuePenaltyNotice(vehicle, charge);
                }
            }
        }
    }

    private BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge = new BigDecimal(0);

        ZoneBoundaryCrossing lastEvent = crossings.get(0);
        double time_temp = 0;
        //int max_time = 4;
        LocalTime first_entry = lastEvent.timestamp();
        int flag = 0;
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing instanceof ExitEvent) {

                if(lastEvent.timestamp().getHour() < 14)
                {

                    time_temp += lastEvent.timestamp().until(crossing.timestamp(), SECONDS);
                    //first_entry = lastEvent.timestamp();

                    if((first_entry.until(crossing.timestamp(),SECONDS)/3600.0) <= 4 && (flag == 1 )) {
                        continue;
                    }
                    else if ((first_entry.until(crossing.timestamp(),SECONDS)/3600.0) <= 4 && (flag == 0 ))
                    {
                        flag = 1;
                        charge = charge.add(new BigDecimal(6));

                    }
                    else {
                        first_entry = lastEvent.timestamp();
                        charge = charge.add(new BigDecimal(6));
                        flag = 1;
                    }
                }
                else{
                    time_temp += lastEvent.timestamp().until(crossing.timestamp(),SECONDS);
                    //first_entry = lastEvent.timestamp();

                    if((first_entry.until(crossing.timestamp(), SECONDS)/3600.0) <= 4 && (flag == 1)) {
                        continue;
                    }
                    else if ((first_entry.until(crossing.timestamp(),SECONDS)/3600.0) <= 4 && (flag == 0)) {
                        flag = 1;
                        charge = charge.add(new BigDecimal(4));

                    }
                    else {
                        first_entry = lastEvent.timestamp();
                        charge = charge.add(new BigDecimal(4));
                        flag = 1;
                    }
                }


            }

            lastEvent = crossing;

        }
        //System.out.println(time_temp);
        if((time_temp/3600.00) > 4){
            charge = new BigDecimal(12);
        }

        return charge;
    }

    private boolean previouslyRegistered(Vehicle vehicle) {
        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (crossing.getVehicle().equals(vehicle)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOrderingOf(List<ZoneBoundaryCrossing> crossings) {

        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing.timestamp().isBefore(lastEvent.timestamp())) {
                return false;
            }
            if (crossing instanceof EntryEvent && lastEvent instanceof EntryEvent) {
                return false;
            }
            if (crossing instanceof ExitEvent && lastEvent instanceof ExitEvent) {
                return false;
            }
            lastEvent = crossing;
        }

        return true;
    }

    private int minutesBetween(long startTimeMs, long endTimeMs) {
        return (int) Math.ceil((endTimeMs - startTimeMs) / (1000.0 * 60.0));
    }
}
