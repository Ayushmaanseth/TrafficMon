package com.trafficmon;

public class Builder {
    private ChargeAlgorithm chargeAlgorithm = new FourHourChargeAlgorithm();
    private AccountsService accountsService = RegisteredCustomerAccountsService.getInstance();
    private PenaltiesService penaltiesService = OperationsTeam.getInstance();

    private Builder() {
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public Builder setChargeAlgorithm(ChargeAlgorithm chargeAlgorithm) {
        this.chargeAlgorithm = chargeAlgorithm;
        return this;
    }

    public Builder setAccountsService(AccountsService accountsService) {
        this.accountsService = accountsService;
        return this;
    }

    public Builder setPenaltiesService(PenaltiesService penaltiesService) {
        this.penaltiesService = penaltiesService;
        return this;
    }

    public CongestionChargeSystem build() {
        return new CongestionChargeSystem(chargeAlgorithm, accountsService, penaltiesService);
    }
}