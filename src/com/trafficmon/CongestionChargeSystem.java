package com.trafficmon;

import java.math.BigDecimal;
import java.util.*;

public class CongestionChargeSystem {

    public static final BigDecimal CHARGE_RATE_POUNDS_PER_MINUTE = new BigDecimal(3000);

    private final List<ZoneBoundaryCrossing> eventLog = new ArrayList<ZoneBoundaryCrossing>();
    //private final AccountsService accountsService;
    //private final PenaltiesService penaltiesService;

    public CongestionChargeSystem() {
        //this.accountsService = accountsService;
        //this.penaltiesService = penaltiesService;
    }


    public void vehicleEnteringZone(Vehicle vehicle,double time) {
        eventLog.add(new EntryEvent(vehicle,time));
    }

    public void vehicleLeavingZone(Vehicle vehicle, double time) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(new ExitEvent(vehicle,time));
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
                //penaltiesService.triggerInvestigationInto(vehicle);
            } else {

                BigDecimal charge = calculateChargeForTimeInZone(crossings);

                try {
                    //accountsService.accountFor(vehicle);
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
        double original_time = lastEvent.timestamp();
        double iterate_time = lastEvent.timestamp();
        int time_temp = 0;
        //int max_time = 4;
        double first_entry = lastEvent.timestamp();
        int flag = 0;
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {


            if (crossing instanceof ExitEvent) {

                if(lastEvent.timestamp() < 14)
                {

                    time_temp += crossing.timestamp() - lastEvent.timestamp();
                    //first_entry = lastEvent.timestamp();


                    if((crossing.timestamp()-first_entry) < 4 && (flag == 1 ))
                    {
                        continue;
                    }
                    else if ((crossing.timestamp()-first_entry) < 4 && (flag == 0 ))
                    {
                        flag = 1;
                        charge = charge.add(new BigDecimal(6));

                    }
                    else {
                        first_entry = lastEvent.timestamp();
                        charge = charge.add(new BigDecimal(6));
                        flag =1;
                    }

//                    if((crossing.timestamp()-first_entry) >= 4 )
//                    {
//                        first_entry = crossing.timestamp();
//                        counter = 0;
//                    }
//



//
//                        counter++;
//                         if(counter == 2 && crossing.timestamp() - original_time < 4){
//                        counter = 0;
//                        original_time = crossing.timestamp();
//                        continue;
//                    }
//                    else{
//                             charge = charge.add(new BigDecimal(6));
//                         }
                }
                else{


                    time_temp += crossing.timestamp() - lastEvent.timestamp();
                    //first_entry = lastEvent.timestamp();


                    if((crossing.timestamp()-first_entry) < 4 && (flag == 1 ))
                    {
                        continue;
                    }
                    else if ((crossing.timestamp()-first_entry) < 4 && (flag == 0 ))
                    {
                        flag = 1;
                        charge = charge.add(new BigDecimal(4));

                    }
                    else {
                        first_entry = lastEvent.timestamp();
                        charge = charge.add(new BigDecimal(4));
                        flag =1;
                    }

//                    counter++;
//                    if(counter == 2 && crossing.timestamp() - original_time < 4) {
//                        counter = 0;
//                        original_time = crossing.timestamp();
//                        continue;
//                    }
//                    else{
//                        charge = charge.add(new BigDecimal(4));
//                    }
                }

                /*
                charge = charge.add(
                        new BigDecimal(minutesBetween(lastEvent.timestamp(), crossing.timestamp()))
                                .multiply(CHARGE_RATE_POUNDS_PER_MINUTE));
                                */
            }

            lastEvent = crossing;

        }

        if(time_temp > 4){
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
            if (crossing.timestamp() < lastEvent.timestamp()) {
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
