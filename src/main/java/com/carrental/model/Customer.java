package com.carrental.model;

public class Customer {
    private final String id;
    private final String name;
    private final CustomerTier loyaltyTier;
    private final boolean ecoFriendlyPreference;

    public Customer(String id, String name, CustomerTier loyaltyTier, boolean ecoFriendlyPreference) {
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier != null ? loyaltyTier : CustomerTier.REGULAR;
        this.ecoFriendlyPreference = ecoFriendlyPreference;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CustomerTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public boolean hasEcoFriendlyPreference() {
        return ecoFriendlyPreference;
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', tier=" + loyaltyTier + ", eco=" + ecoFriendlyPreference + "}";
    }
}
