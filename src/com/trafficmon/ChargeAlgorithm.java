package com.trafficmon;

import java.math.BigDecimal;
import java.util.List;

public interface ChargeAlgorithm {
    BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings);
}
