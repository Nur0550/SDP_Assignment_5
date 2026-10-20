package com.carrental;

import com.carrental.model.*;
import com.carrental.provider.FakeMarketDemandProvider;
import com.carrental.selector.PricingStrategySelector;
import com.carrental.selector.VehicleAllocationStrategySelector;
import com.carrental.strategy.allocation.*;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SelectorsTest {

    private FakeMarketDemandProvider fakeDemandProvider;
    private PricingStrategySelector pricingSelector;
    private VehicleAllocationStrategySelector allocationSelector;
    private Vehicle sampleCar;
    private Customer regularCustomer;
    private Customer vipCustomer;
    private Customer ecoCustomer;

    @BeforeEach
    void setUp() {
        fakeDemandProvider = new FakeMarketDemandProvider(1.0, false, 0.0);
        pricingSelector = new PricingStrategySelector(fakeDemandProvider);
        allocationSelector = new VehicleAllocationStrategySelector();

        sampleCar = new Vehicle("V-1", "Sedan", VehicleCategory.COMFORT, FuelType.GASOLINE, 50.0, 6.0, 140.0, 7, 5, true);
        regularCustomer = new Customer("C-1", "Regular Dan", CustomerTier.REGULAR, false);
        vipCustomer = new Customer("C-2", "VIP Victoria", CustomerTier.VIP, false);
        ecoCustomer = new Customer("C-3", "Eco Ethan", CustomerTier.SILVER, true);
    }

    @Test
    @DisplayName("PricingStrategySelector Rule 1: selects LongTerm strategy when rental duration >= 7 days")
    void testPricingSelectorRuleLongTerm() {
        RentalBooking booking = new RentalBooking(
                "B-1", regularCustomer, sampleCar, 7, 700.0, LocalDate.now(), "Center"
        );
        RentalPricingStrategy selected = pricingSelector.selectStrategy(booking);
        assertInstanceOf(LongTermSubscriptionPricingStrategy.class, selected);
    }

    @Test
    @DisplayName("PricingStrategySelector Rule 2A: selects DynamicDemand strategy when surge multiplier > 1.15")
    void testPricingSelectorRuleSurgeDemand() {
        fakeDemandProvider.setSurgeMultiplier(1.25);
        RentalBooking booking = new RentalBooking(
                "B-2", regularCustomer, sampleCar, 3, 300.0, LocalDate.now(), "Center"
        );
        RentalPricingStrategy selected = pricingSelector.selectStrategy(booking);
        assertInstanceOf(DynamicDemandPricingStrategy.class, selected);
    }

    @Test
    @DisplayName("PricingStrategySelector Rule 2B: selects DynamicDemand strategy when weather warning is active")
    void testPricingSelectorRuleWeatherWarning() {
        fakeDemandProvider.setSurgeMultiplier(1.0);
        fakeDemandProvider.setWeatherWarning(true);
        RentalBooking booking = new RentalBooking(
                "B-3", regularCustomer, sampleCar, 2, 200.0, LocalDate.now(), "Center"
        );
        RentalPricingStrategy selected = pricingSelector.selectStrategy(booking);
        assertInstanceOf(DynamicDemandPricingStrategy.class, selected);
    }

    @Test
    @DisplayName("PricingStrategySelector Rule 3: defaults to StandardDailyPricingStrategy under normal conditions")
    void testPricingSelectorRuleStandardDefault() {
        fakeDemandProvider.setSurgeMultiplier(1.0);
        fakeDemandProvider.setWeatherWarning(false);
        RentalBooking booking = new RentalBooking(
                "B-4", regularCustomer, sampleCar, 3, 300.0, LocalDate.now(), "Center"
        );
        RentalPricingStrategy selected = pricingSelector.selectStrategy(booking);
        assertInstanceOf(StandardDailyPricingStrategy.class, selected);
    }

    @Test
    @DisplayName("VehicleAllocationStrategySelector Rule 1: selects EcoFriendly strategy when eco preference is present")
    void testAllocationSelectorRuleEco() {
        RentalCriteria criteria = new RentalCriteria(5, 100.0, ecoCustomer, false);
        VehicleAllocationStrategy selected = allocationSelector.selectStrategy(criteria);
        assertInstanceOf(EcoFriendlyAllocationStrategy.class, selected);
    }

    @Test
    @DisplayName("VehicleAllocationStrategySelector Rule 2: selects LoyaltyTier strategy for VIP/GOLD members")
    void testAllocationSelectorRuleVIP() {
        RentalCriteria criteria = new RentalCriteria(5, 150.0, vipCustomer, false);
        VehicleAllocationStrategy selected = allocationSelector.selectStrategy(criteria);
        assertInstanceOf(LoyaltyTierAllocationStrategy.class, selected);
    }

    @Test
    @DisplayName("VehicleAllocationStrategySelector Rule 3: defaults to EconomyFirst strategy for regular customers")
    void testAllocationSelectorRuleEconomyDefault() {
        RentalCriteria criteria = new RentalCriteria(5, 80.0, regularCustomer, false);
        VehicleAllocationStrategy selected = allocationSelector.selectStrategy(criteria);
        assertInstanceOf(EconomyFirstAllocationStrategy.class, selected);
    }
}
