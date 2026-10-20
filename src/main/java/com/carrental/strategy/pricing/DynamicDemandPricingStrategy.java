package com.carrental.strategy.pricing;

import com.carrental.model.RentalBooking;
import com.carrental.provider.DemandMetrics;
import com.carrental.provider.MarketDemandProvider;

/**
 * Dynamic demand pricing strategy.
 * Uses external market and weather intelligence to dynamically adjust pricing
 * based on surge demand, adverse weather, and regional risk factors.
 */
public class DynamicDemandPricingStrategy implements RentalPricingStrategy {

    private final MarketDemandProvider demandProvider;

    public DynamicDemandPricingStrategy(MarketDemandProvider demandProvider) {
        if (demandProvider == null) {
            throw new IllegalArgumentException("MarketDemandProvider cannot be null");
        }
        this.demandProvider = demandProvider;
    }

    @Override
    public double calculatePrice(RentalBooking booking) {
        DemandMetrics metrics = demandProvider.getDemandMetrics(booking.getLocation(), booking.getStartDate());
        double base = booking.getVehicle().getDailyRate() * booking.getRentalDays();

        double surgePrice = base * metrics.getSurgeMultiplier();
        double weatherSurcharge = metrics.hasWeatherWarning() ? 35.0 : 0.0;
        double riskSurcharge = metrics.getRiskIndex() * 10.0;

        return Math.round((surgePrice + weatherSurcharge + riskSurcharge) * 100.0) / 100.0;
    }
}
