package com.trafficmon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcreteWrapper implements Wrapper {

    private List<Account> accounts;
    private static final ConcreteWrapper INSTANCE = new ConcreteWrapper();

    private ConcreteWrapper(){
        accounts = new ArrayList<Account>();
        accounts.add(new Account("ABC",Vehicle.withRegistration("Test Vehicle"),new BigDecimal(10)));
        accounts.add(new Account("DEF",Vehicle.withRegistration("Test"),new BigDecimal(20)));
        accounts.add(new Account("Nishan Singh",Vehicle.withRegistration("SH06 UVB"),new BigDecimal(0)));
        accounts.add(new Account("Sanjay Seth",Vehicle.withRegistration("HRAU41275"),new BigDecimal(10)));
    }

    @Override
    public Account accountFor(Vehicle vehicle) throws AccountNotRegisteredException {
        Iterator i$ = this.accounts.iterator();

        Account account;
        do {
            if (!i$.hasNext()) {
                throw new AccountNotRegisteredException(vehicle);
            }

            account = (Account)i$.next();
        } while(!account.getAssociatedVehicle().equals(vehicle));

        return account;
    }

    @Override
    public void triggerInvestigationInto(Vehicle vehicle) {
        System.out.println("Mismatched entries/exits. Triggering investigation into vehicle: " + vehicle);
    }

    @Override
    public void issuePenaltyNotice(Vehicle vehicle, BigDecimal charge) {
        System.out.println("Penalty notice for: " + vehicle);
    }

    public static ConcreteWrapper getInstance() {
        return INSTANCE;
    }
}
