package com.carrental.model;

public class RentalCriteria {
    private final int minimumSeats;
    private final double maxDailyBudget;
    private final Customer customer;
    private final boolean ecoFriendlyOnly;

    public RentalCriteria(int minimumSeats, double maxDailyBudget, Customer customer, boolean ecoFriendlyOnly) {
        if (minimumSeats <= 0) {
            throw new IllegalArgumentException("Minimum seats must be positive");
        }
        if (maxDailyBudget <= 0) {
            throw new IllegalArgumentException("Maximum daily budget must be positive");
        }
        this.minimumSeats = minimumSeats;
        this.maxDailyBudget = maxDailyBudget;
        this.customer = customer;
        this.ecoFriendlyOnly = ecoFriendlyOnly;
    }

    public int getMinimumSeats() {
        return minimumSeats;
    }

    public double getMaxDailyBudget() {
        return maxDailyBudget;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isEcoFriendlyOnly() {
        return ecoFriendlyOnly;
    }
}
