package com.trafficmon;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class FourHourChargeAlgorithm implements ChargeAlgorithm{
    public FourHourChargeAlgorithm() {
    }

    public BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge = new BigDecimal(0);
        ZoneBoundaryCrossing lastEvent = crossings.get(0);
        double totalTime = 0;
        LocalTime first_entry = lastEvent.timestamp();
        int flag = 0;
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing.getTypeofEvent().equals("Exit")) {

                ChargeHelper chargeHelper = new ChargeHelper(charge, lastEvent, totalTime, first_entry, flag, crossing).invoke();
                if (chargeHelper.is()) {
                    continue;
                }
                charge = chargeHelper.getCharge();
                totalTime = chargeHelper.getTotalTime();
                first_entry = chargeHelper.getFirst_entry();
                flag = chargeHelper.getFlag();
            }
            lastEvent = crossing;
        }
        //System.out.println(totalTime);
        if ((totalTime / 3600.00) > 4) {
            charge = new BigDecimal(12);
        }

        return charge;
    }

    class ChargeHelper {
        private boolean myResult;
        private BigDecimal charge;
        private ZoneBoundaryCrossing lastEvent;
        private double totalTime;
        private LocalTime first_entry;
        private int flag;
        private ZoneBoundaryCrossing crossing;

        public ChargeHelper(BigDecimal charge, ZoneBoundaryCrossing lastEvent, double totalTime, LocalTime first_entry, int flag, ZoneBoundaryCrossing crossing) {
            this.charge = charge;
            this.lastEvent = lastEvent;
            this.totalTime = totalTime;
            this.first_entry = first_entry;
            this.flag = flag;
            this.crossing = crossing;
        }

        boolean is() {
            return myResult;
        }

        public BigDecimal getCharge() {
            return charge;
        }

        public double getTotalTime() {
            return totalTime;
        }

        public LocalTime getFirst_entry() {
            return first_entry;
        }

        public int getFlag() {
            return flag;
        }

        public ChargeHelper invoke() {
            if(lastEvent.timestamp().getHour() < 14) {
                if (chargeTester(6)) return this;
            }
            else{
                if (chargeTester(4)) return this;
            }
            myResult = false;
            return this;
        }

        public boolean chargeTester(int chargeToAdd) {
            totalTime += lastEvent.timestamp().until(crossing.timestamp(), SECONDS);

            if ((first_entry.until(crossing.timestamp(), SECONDS) / 3600.0) <= 4 && (flag == 1)) {
                myResult = true;
                return true;
            } else if ((first_entry.until(crossing.timestamp(), SECONDS) / 3600.0) <= 4 && (flag == 0)) {
                flag = 1;
                charge = addCharge(charge, chargeToAdd);
            } else {
                first_entry = lastEvent.timestamp();
                charge = addCharge(charge, chargeToAdd);
                flag = 1;
            }
            return false;
        }

        private BigDecimal addCharge(BigDecimal charge, int addedCharge ) {
            charge = charge.add(new BigDecimal(addedCharge));
            return charge;
        }
    }
}