package com.carrental.strategy.pricing;

import com.carrental.model.RentalBooking;

/**
 * Context class for the rental pricing strategy family.
 * Holds a reference to a RentalPricingStrategy and delegates calculation to it.
 * Strictly adheres to the Strategy pattern by omitting any conditional logic
 * (no if, switch, or instanceof checks).
 */
public class RentalPricingContext {

    private RentalPricingStrategy strategy;

    public RentalPricingContext(RentalPricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Initial pricing strategy cannot be null");
        }
        this.strategy = strategy;
    }

    public void setStrategy(RentalPricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be set to null");
        }
        this.strategy = strategy;
    }

    public RentalPricingStrategy getStrategy() {
        return strategy;
    }

    public double executePricing(RentalBooking booking) {
        return strategy.calculatePrice(booking);
    }
}
