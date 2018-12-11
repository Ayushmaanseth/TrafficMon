package com.trafficmon;

import java.math.BigDecimal;

public interface Wrapper extends AccountsService,PenaltiesService {
    Account accountFor(Vehicle vehicle) throws AccountNotRegisteredException;
    void triggerInvestigationInto(Vehicle vehicle);
    void issuePenaltyNotice(Vehicle vehicle, BigDecimal charge);
}

