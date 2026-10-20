package com.carrental.demo;

import com.carrental.model.*;
import com.carrental.provider.MarketDemandProvider;
import com.carrental.provider.SimulatedMarketDemandProvider;
import com.carrental.selector.PricingStrategySelector;
import com.carrental.selector.VehicleAllocationStrategySelector;
import com.carrental.strategy.allocation.*;
import com.carrental.strategy.pricing.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Interactive demonstration showcasing Strategy Pattern features:
 * 1. Same input processed by all strategies of a family.
 * 2. Manual choice vs. automatic choice by Selector.
 * 3. Strategy switching at runtime.
 */
public class CarRentalDemo {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            CAR RENTAL SYSTEM: STRATEGY PATTERN ARCHITECTURE DEMO");
        System.out.println("================================================================================");

        MarketDemandProvider demandProvider = new SimulatedMarketDemandProvider();

        // Sample Fleet
        List<Vehicle> fleet = Arrays.asList(
                new Vehicle("V-1", "Nissan Versa", VehicleCategory.ECONOMY, FuelType.GASOLINE, 35.0, 6.2, 140.0, 4, 5, true),
                new Vehicle("V-2", "Tesla Model 3", VehicleCategory.COMFORT, FuelType.ELECTRIC, 75.0, 0.0, 0.0, 8, 5, true),
                new Vehicle("V-3", "Toyota Prius Hybrid", VehicleCategory.ECONOMY, FuelType.HYBRID, 45.0, 4.1, 75.0, 6, 5, true),
                new Vehicle("V-4", "BMW X5 xDrive", VehicleCategory.SUV, FuelType.DIESEL, 120.0, 8.5, 190.0, 9, 7, true),
                new Vehicle("V-5", "Mercedes-Benz S-Class", VehicleCategory.LUXURY, FuelType.GASOLINE, 180.0, 10.0, 220.0, 10, 5, true)
        );

        Customer regularCustomer = new Customer("C-1", "John Doe", CustomerTier.REGULAR, false);
        Customer vipCustomer = new Customer("C-2", "Elena Rostova", CustomerTier.VIP, false);
        Customer ecoCustomer = new Customer("C-3", "Lucas Green", CustomerTier.SILVER, true);

        // =============================================================================
        // DEMO PART 1: The same input processed by all strategies of a family
        // =============================================================================
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("PART 1: The same input processed by all strategies of Family 1 (Pricing)");
        System.out.println("--------------------------------------------------------------------------------");

        Vehicle testCar = fleet.get(0); // Nissan Versa ($35/day)
        RentalBooking sampleBooking = new RentalBooking(
                "BOOK-101", regularCustomer, testCar, 10, 1800.0,
                LocalDate.of(2026, 7, 10), "Downtown"
        );

        RentalPricingStrategy standardStrategy = new StandardDailyPricingStrategy();
        RentalPricingStrategy longTermStrategy = new LongTermSubscriptionPricingStrategy();
        RentalPricingStrategy dynamicDemandStrategy = new DynamicDemandPricingStrategy(demandProvider);

        RentalPricingContext pricingContext = new RentalPricingContext(standardStrategy);

        System.out.printf("Booking details: 10 days, 1800 km, Car: %s ($%.2f/day)\n",
                testCar.getModelName(), testCar.getDailyRate());

        // Standard
        pricingContext.setStrategy(standardStrategy);
        double stdPrice = pricingContext.executePricing(sampleBooking);
        System.out.printf("  [StandardDailyPricingStrategy]          : $%.2f (includes base + km allowance + weekend fee)\n", stdPrice);

        // Long-term
        pricingContext.setStrategy(longTermStrategy);
        double ltPrice = pricingContext.executePricing(sampleBooking);
        System.out.printf("  [LongTermSubscriptionPricingStrategy]   : $%.2f (includes 15%% 10-day discount - $50 credit)\n", ltPrice);

        // Dynamic Demand
        pricingContext.setStrategy(dynamicDemandStrategy);
        double dynPrice = pricingContext.executePricing(sampleBooking);
        System.out.printf("  [DynamicDemandPricingStrategy]          : $%.2f (includes live demand multiplier & weather)\n", dynPrice);

        // =============================================================================
        // DEMO PART 2: Manual choice vs. Automatic choice by Selector
        // =============================================================================
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("PART 2: Manual choice vs. Automatic choice by Selectors");
        System.out.println("--------------------------------------------------------------------------------");

        PricingStrategySelector pricingSelector = new PricingStrategySelector(demandProvider);
        VehicleAllocationStrategySelector allocationSelector = new VehicleAllocationStrategySelector();

        // Scenario A: Short 3-day booking in summer surge season
        RentalBooking summerBooking = new RentalBooking(
                "BOOK-SUMMER", regularCustomer, testCar, 3, 200.0,
                LocalDate.of(2026, 7, 17), "Airport"
        );
        RentalPricingStrategy autoSelectedPriceStrategy = pricingSelector.selectStrategy(summerBooking);
        System.out.println("Scenario A: Short 3-day summer peak rental");
        System.out.println("  -> Manual default would be: StandardDailyPricingStrategy");
        System.out.println("  -> Selector automatic choice: " + autoSelectedPriceStrategy.getClass().getSimpleName());

        // Scenario B: Extended 35-day monthly rental
        RentalBooking monthlyBooking = new RentalBooking(
                "BOOK-MONTHLY", regularCustomer, testCar, 35, 3000.0,
                LocalDate.of(2026, 9, 1), "Downtown"
        );
        RentalPricingStrategy autoMonthlyStrategy = pricingSelector.selectStrategy(monthlyBooking);
        System.out.println("Scenario B: 35-day extended duration rental");
        System.out.println("  -> Selector automatic choice: " + autoMonthlyStrategy.getClass().getSimpleName());

        // Scenario C: Vehicle Allocation for VIP Customer with budget $150
        RentalCriteria vipCriteria = new RentalCriteria(5, 150.0, vipCustomer, false);
        VehicleAllocationStrategy autoAllocStrategyVIP = allocationSelector.selectStrategy(vipCriteria);
        VehicleAllocationContext allocContext = new VehicleAllocationContext(autoAllocStrategyVIP);
        Optional<Vehicle> allocatedForVIP = allocContext.executeAllocation(fleet, vipCriteria);

        System.out.println("Scenario C: Fleet allocation for VIP Member (Elena Rostova, budget $150)");
        System.out.println("  -> Selector automatic choice: " + autoAllocStrategyVIP.getClass().getSimpleName());
        allocatedForVIP.ifPresent(v -> System.out.println("  -> Allocated vehicle: " + v));

        // Scenario D: Vehicle Allocation for Eco-conscious Customer
        RentalCriteria ecoCriteria = new RentalCriteria(5, 80.0, ecoCustomer, true);
        VehicleAllocationStrategy autoAllocStrategyEco = allocationSelector.selectStrategy(ecoCriteria);
        allocContext.setStrategy(autoAllocStrategyEco);
        Optional<Vehicle> allocatedForEco = allocContext.executeAllocation(fleet, ecoCriteria);

        System.out.println("Scenario D: Fleet allocation for Green Traveler (Lucas Green, eco-friendly)");
        System.out.println("  -> Selector automatic choice: " + autoAllocStrategyEco.getClass().getSimpleName());
        allocatedForEco.ifPresent(v -> System.out.println("  -> Allocated vehicle: " + v));

        // =============================================================================
        // DEMO PART 3: Switching the strategy at runtime
        // =============================================================================
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("PART 3: Dynamic Strategy Switching at Runtime");
        System.out.println("--------------------------------------------------------------------------------");

        RentalCriteria standardCriteria = new RentalCriteria(5, 100.0, regularCustomer, false);
        System.out.println("Evaluating same fleet and customer criteria through 3 different allocation strategies at runtime:\n");

        allocContext.setStrategy(new EconomyFirstAllocationStrategy());
        System.out.println("1. Runtime strategy set to: " + allocContext.getStrategy().getClass().getSimpleName());
        allocContext.executeAllocation(fleet, standardCriteria).ifPresent(v -> System.out.println("   Result -> " + v));

        allocContext.setStrategy(new EcoFriendlyAllocationStrategy());
        System.out.println("2. Runtime switched strategy to: " + allocContext.getStrategy().getClass().getSimpleName());
        allocContext.executeAllocation(fleet, standardCriteria).ifPresent(v -> System.out.println("   Result -> " + v));

        allocContext.setStrategy(new LoyaltyTierAllocationStrategy());
        System.out.println("3. Runtime switched strategy to: " + allocContext.getStrategy().getClass().getSimpleName());
        allocContext.executeAllocation(fleet, standardCriteria).ifPresent(v -> System.out.println("   Result -> " + v));

        System.out.println("\n================================================================================");
        System.out.println("                   STRATEGY PATTERN DEMO FINISHED SUCCESSFULLY");
        System.out.println("================================================================================");
    }
}
