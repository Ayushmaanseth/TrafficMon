package com.trafficmon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.trafficmon.ZoneBoundaryCrossing.*;

public class CongestionChargeSystem {

    private final List<ZoneBoundaryCrossing> eventLog = new ArrayList<>();

    private final AccountsService accountsService;
    private final PenaltiesService penaltiesService;
    private final ChargeAlgorithm chargeAlgorithm;

    public CongestionChargeSystem(ChargeAlgorithm chargeAlgorithm, AccountsService accountsService,PenaltiesService penaltiesService){
        this.penaltiesService = penaltiesService;
        this.accountsService = accountsService;
        this.chargeAlgorithm = chargeAlgorithm;
    }

    public void vehicleEnteringZone(Vehicle vehicle) {
        eventLog.add(createEntryEvent(vehicle));
    }

    public void vehicleEnteringZone(Vehicle vehicle,Clock clock) {
        eventLog.add(createEntryEvent(vehicle,clock));
    }

    public void vehicleLeavingZone(Vehicle vehicle) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(createExitEvent(vehicle));
    }


    public void vehicleLeavingZone(Vehicle vehicle,Clock clock) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(createExitEvent(vehicle,clock));
    }


    public  void calculateCharges() {

        Map<Vehicle, List<ZoneBoundaryCrossing>> crossingsByVehicle;
        crossingsByVehicle = new HashMap<>();

        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (!crossingsByVehicle.containsKey(crossing.getVehicle())) {
                crossingsByVehicle.put(crossing.getVehicle(), new ArrayList<>());
            }
            crossingsByVehicle.get(crossing.getVehicle()).add(crossing);
        }

        for (Map.Entry<Vehicle, List<ZoneBoundaryCrossing>> vehicleCrossings : crossingsByVehicle.entrySet()) {
            Vehicle vehicle = vehicleCrossings.getKey();
            List<ZoneBoundaryCrossing> crossings = vehicleCrossings.getValue();

            //OperationsTeam.getInstance().triggerInvestigationInto(vehicle);
            if (!checkOrderingOf(crossings)) {
                penaltiesService.triggerInvestigationInto(vehicle);
            } else {


                BigDecimal charge = calculateChargeForTimeInZone(crossings);


                try {
                    accountsService.accountFor(vehicle).deduct(charge);
                } catch (InsufficientCreditException | AccountNotRegisteredException ice) {
                    penaltiesService.issuePenaltyNotice(vehicle, charge);
                }
            }
        }
    }

    private BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings) {
        return chargeAlgorithm.calculateChargeForTimeInZone(crossings);
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
            if (!checkInstanceOrdering(lastEvent, crossing, "Entry") || !checkInstanceOrdering(lastEvent, crossing, "Exit")) {
                return false;
            }

            lastEvent = crossing;
        }

        return true;
    }

    private boolean checkInstanceOrdering(ZoneBoundaryCrossing lastEvent, ZoneBoundaryCrossing crossing, String typeOfEvent){
        if (crossing.getTypeofEvent().equals(typeOfEvent)) return true;
        return lastEvent.getTypeofEvent().equals(typeOfEvent);
    }

}
