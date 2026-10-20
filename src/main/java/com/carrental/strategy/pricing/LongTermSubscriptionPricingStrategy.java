package com.carrental.strategy.pricing;

import com.carrental.model.RentalBooking;

/**
 * Long-term subscription rental pricing strategy.
 * Offers tiered volume duration discounts, unlimited mileage,
 * and complimentary maintenance credits.
 */
public class LongTermSubscriptionPricingStrategy implements RentalPricingStrategy {

    private static final double MAINTENANCE_CREDIT = 50.0;

    @Override
    public double calculatePrice(RentalBooking booking) {
        double dailyRate = booking.getVehicle().getDailyRate();
        int days = booking.getRentalDays();
        double gross = dailyRate * days;

        double discountRate;
        if (days >= 30) {
            discountRate = 0.25;
        } else if (days >= 7) {
            discountRate = 0.15;
        } else {
            discountRate = 0.05;
        }

        double discounted = gross * (1.0 - discountRate);
        double net = discounted - MAINTENANCE_CREDIT;
        double minThreshold = dailyRate;

        return Math.round(Math.max(net, minThreshold) * 100.0) / 100.0;
    }
}
