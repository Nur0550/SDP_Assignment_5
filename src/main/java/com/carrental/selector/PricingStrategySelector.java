package com.carrental.selector;

import com.carrental.model.RentalBooking;
import com.carrental.provider.DemandMetrics;
import com.carrental.provider.MarketDemandProvider;
import com.carrental.strategy.pricing.DynamicDemandPricingStrategy;
import com.carrental.strategy.pricing.LongTermSubscriptionPricingStrategy;
import com.carrental.strategy.pricing.RentalPricingStrategy;
import com.carrental.strategy.pricing.StandardDailyPricingStrategy;

/**
 * Selector responsible for automatically picking the optimal pricing strategy
 * based on booking duration, market demand metrics, and weather conditions.
 * Conditional logic (if / switch) is explicitly permitted here.
 */
public class PricingStrategySelector {

    private final RentalPricingStrategy standardStrategy;
    private final RentalPricingStrategy longTermStrategy;
    private final RentalPricingStrategy dynamicDemandStrategy;
    private final MarketDemandProvider demandProvider;

    public PricingStrategySelector(MarketDemandProvider demandProvider) {
        if (demandProvider == null) {
            throw new IllegalArgumentException("MarketDemandProvider cannot be null");
        }
        this.demandProvider = demandProvider;
        this.standardStrategy = new StandardDailyPricingStrategy();
        this.longTermStrategy = new LongTermSubscriptionPricingStrategy();
        this.dynamicDemandStrategy = new DynamicDemandPricingStrategy(demandProvider);
    }

    public RentalPricingStrategy selectStrategy(RentalBooking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        // Rule 1: Extended rental durations automatically qualify for long-term subscription pricing
        if (booking.getRentalDays() >= 7) {
            return longTermStrategy;
        }

        // Rule 2: Market surge or adverse weather conditions activate dynamic demand pricing
        DemandMetrics metrics = demandProvider.getDemandMetrics(booking.getLocation(), booking.getStartDate());
        if (metrics.getSurgeMultiplier() > 1.15 || metrics.hasWeatherWarning()) {
            return dynamicDemandStrategy;
        }

        // Rule 3: Default regular short-term rentals use standard daily pricing
        return standardStrategy;
    }

    public RentalPricingStrategy getStandardStrategy() {
        return standardStrategy;
    }

    public RentalPricingStrategy getLongTermStrategy() {
        return longTermStrategy;
    }

    public RentalPricingStrategy getDynamicDemandStrategy() {
        return dynamicDemandStrategy;
    }
}
