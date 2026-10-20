package com.carrental.service;

import com.carrental.model.RentalBooking;
import com.carrental.provider.MarketDemandProvider;
import com.carrental.strategy.pricing.DynamicDemandPricingStrategy;
import com.carrental.strategy.pricing.LongTermSubscriptionPricingStrategy;
import com.carrental.strategy.pricing.RentalPricingContext;
import com.carrental.strategy.pricing.RentalPricingStrategy;
import com.carrental.strategy.pricing.StandardDailyPricingStrategy;

import java.util.EnumMap;
import java.util.Map;

/**
 * Refactored pricing service.
 * Delegates calculation to concrete strategies via RentalPricingContext,
 * eliminating all monolithic algorithmic calculations from this class.
 */
public class RentalPriceCalculator {

    private final Map<RentalPricingType, RentalPricingStrategy> strategyMap;
    private final RentalPricingContext context;

    public RentalPriceCalculator(MarketDemandProvider demandProvider) {
        this.strategyMap = new EnumMap<>(RentalPricingType.class);
        strategyMap.put(RentalPricingType.STANDARD, new StandardDailyPricingStrategy());
        strategyMap.put(RentalPricingType.LONG_TERM, new LongTermSubscriptionPricingStrategy());
        strategyMap.put(RentalPricingType.DYNAMIC_DEMAND, new DynamicDemandPricingStrategy(demandProvider));

        // Initialize context with default strategy
        this.context = new RentalPricingContext(strategyMap.get(RentalPricingType.STANDARD));
    }

    public double calculatePrice(RentalBooking booking, RentalPricingType pricingType) {
        if (pricingType == null) {
            throw new IllegalArgumentException("Pricing type cannot be null");
        }
        RentalPricingStrategy strategy = strategyMap.get(pricingType);
        if (strategy == null) {
            throw new UnsupportedOperationException("Unknown pricing type: " + pricingType);
        }

        context.setStrategy(strategy);
        return context.executePricing(booking);
    }

    public RentalPricingContext getContext() {
        return context;
    }
}
