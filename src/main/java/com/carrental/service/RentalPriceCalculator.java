package com.carrental.service;

import com.carrental.model.RentalBooking;
import com.carrental.provider.DemandMetrics;
import com.carrental.provider.MarketDemandProvider;

import java.time.DayOfWeek;

/**
 * Monolithic pricing calculator using switch-case.
 * Intentionally written as the initial "bad" implementation for refactoring comparison.
 */
public class RentalPriceCalculator {
    private final MarketDemandProvider demandProvider;

    public RentalPriceCalculator(MarketDemandProvider demandProvider) {
        this.demandProvider = demandProvider;
    }

    public double calculatePrice(RentalBooking booking, RentalPricingType pricingType) {
        if (pricingType == null) {
            throw new IllegalArgumentException("Pricing type cannot be null");
        }

        switch (pricingType) {
            case STANDARD: {
                double base = booking.getVehicle().getDailyRate() * booking.getRentalDays();
                // Mileage allowance: 150 km per day. Excess kilometers cost $0.25/km
                double allowedKm = booking.getRentalDays() * 150.0;
                double extraKmFee = 0.0;
                if (booking.getEstimatedKilometers() > allowedKm) {
                    extraKmFee = (booking.getEstimatedKilometers() - allowedKm) * 0.25;
                }
                // Weekend surcharge: 15% if rental period includes Saturday or Sunday
                boolean includesWeekend = false;
                for (int i = 0; i < booking.getRentalDays(); i++) {
                    DayOfWeek dow = booking.getStartDate().plusDays(i).getDayOfWeek();
                    if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                        includesWeekend = true;
                        break;
                    }
                }
                double weekendSurcharge = includesWeekend ? base * 0.15 : 0.0;
                return Math.round((base + extraKmFee + weekendSurcharge) * 100.0) / 100.0;
            }

            case LONG_TERM: {
                double dailyRate = booking.getVehicle().getDailyRate();
                int days = booking.getRentalDays();
                double gross = dailyRate * days;

                // Tiered long-term volume discount: >=30 days: 25% off, >=7 days: 15% off, otherwise 5%
                double discountRate;
                if (days >= 30) {
                    discountRate = 0.25;
                } else if (days >= 7) {
                    discountRate = 0.15;
                } else {
                    discountRate = 0.05;
                }
                double discounted = gross * (1.0 - discountRate);

                // Complimentary periodic maintenance and insurance waiver credit
                double maintenanceCredit = 50.0;
                double net = discounted - maintenanceCredit;

                // Ensure price never drops below minimum deposit threshold (at least 1 day base rate)
                double minThreshold = dailyRate;
                return Math.round(Math.max(net, minThreshold) * 100.0) / 100.0;
            }

            case DYNAMIC_DEMAND: {
                DemandMetrics metrics = demandProvider.getDemandMetrics(booking.getLocation(), booking.getStartDate());
                double base = booking.getVehicle().getDailyRate() * booking.getRentalDays();

                // Apply dynamic surge multiplier from market demand
                double surgePrice = base * metrics.getSurgeMultiplier();

                // Weather hazard fee if adverse conditions
                double weatherSurcharge = metrics.hasWeatherWarning() ? 35.0 : 0.0;

                // Regional risk surcharge
                double riskSurcharge = metrics.getRiskIndex() * 10.0;

                return Math.round((surgePrice + weatherSurcharge + riskSurcharge) * 100.0) / 100.0;
            }

            default:
                throw new UnsupportedOperationException("Unknown pricing type: " + pricingType);
        }
    }
}
