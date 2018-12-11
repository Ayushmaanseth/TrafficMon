package com.trafficmon;

import java.math.BigDecimal;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

public class OldAlgorithm implements ChargeAlgorithm {

    public static BigDecimal CHARGE_RATE_POUNDS_PER_MINUTE = new BigDecimal(0.05);

    public BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge = new BigDecimal(0);

        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {

            if (crossing.getTypeofEvent().equals("Exit")) {
                charge = charge.add(
                        new BigDecimal(lastEvent.timestamp().until(crossing.timestamp(), MINUTES))
                                .multiply(CHARGE_RATE_POUNDS_PER_MINUTE));
            }

            lastEvent = crossing;
        }

        return charge;
    }

}