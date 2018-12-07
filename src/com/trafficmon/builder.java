package com.trafficmon;

public class builder {
    private ChargeAlgorithm chargeAlgorithm = new FourHourChargeAlgorithm();
    private AccountsService accountsService = RegisteredCustomerAccountsService.getInstance();
    private PenaltiesService penaltiesService = OperationsTeam.getInstance();

    public builder setChargeAlgorithm(ChargeAlgorithm chargeAlgorithm) {
        this.chargeAlgorithm = chargeAlgorithm;
        return this;
    }

    public builder setAccountsService(AccountsService accountsService) {
        this.accountsService = accountsService;
        return this;
    }

    public builder setPenaltiesService(PenaltiesService penaltiesService) {
        this.penaltiesService = penaltiesService;
        return this;
    }

    public CongestionChargeSystem build() {
        return new CongestionChargeSystem(chargeAlgorithm, accountsService, penaltiesService);
    }
}