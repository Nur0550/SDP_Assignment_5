package com.carrental;

import com.carrental.model.*;
import com.carrental.provider.FakeMarketDemandProvider;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PricingStrategiesTest {

    private Vehicle standardVehicle;
    private Customer testCustomer;
    private FakeMarketDemandProvider fakeProvider;

    @BeforeEach
    void setUp() {
        standardVehicle = new Vehicle(
                "V-TEST", "Standard Sedan", VehicleCategory.COMFORT, FuelType.GASOLINE,
                60.0, 7.0, 150.0, 7, 5, true
        );
        testCustomer = new Customer("C-TEST", "Bob Miller", CustomerTier.REGULAR, false);
        fakeProvider = new FakeMarketDemandProvider(1.0, false, 0.0);
    }

    @Test
    @DisplayName("StandardDailyPricingStrategy: verifies weekday base calculation with no excess km")
    void testStandardStrategyWeekdayNoExcess() {
        StandardDailyPricingStrategy strategy = new StandardDailyPricingStrategy();
        // 2 days weekday (Tuesday to Wednesday)
        RentalBooking booking = new RentalBooking(
                "B-1", testCustomer, standardVehicle, 2, 200.0, LocalDate.of(2026, 10, 20), "Center"
        );
        // 2 days * $60 = $120.0 (no weekend fee, 200 km <= 300 km allowed)
        assertEquals(120.0, strategy.calculatePrice(booking), 0.01);
    }

    @Test
    @DisplayName("StandardDailyPricingStrategy: verifies weekend surcharge and excess mileage charge")
    void testStandardStrategyWeekendAndExcessMileage() {
        StandardDailyPricingStrategy strategy = new StandardDailyPricingStrategy();
        // 2 days over weekend (Saturday to Sunday), 400 km driven (allowed = 300 km, excess = 100 km)
        RentalBooking booking = new RentalBooking(
                "B-2", testCustomer, standardVehicle, 2, 400.0, LocalDate.of(2026, 10, 24), "Center"
        );
        // Base = $120.0
        // Weekend fee = 120 * 0.15 = $18.0
        // Excess km = (400 - 300) * 0.25 = $25.0
        // Total = 120 + 18 + 25 = $163.0
        assertEquals(163.0, strategy.calculatePrice(booking), 0.01);
    }

    @Test
    @DisplayName("LongTermSubscriptionPricingStrategy: verifies weekly and monthly tiered discounts")
    void testLongTermStrategyTiers() {
        LongTermSubscriptionPricingStrategy strategy = new LongTermSubscriptionPricingStrategy();

        // 7 days rental: 15% discount, -$50 maintenance credit
        RentalBooking weekBooking = new RentalBooking(
                "B-3", testCustomer, standardVehicle, 7, 1000.0, LocalDate.of(2026, 10, 1), "Center"
        );
        // Gross = 7 * 60 = 420. 85% = 357.0. Net = 357 - 50 = $307.0
        assertEquals(307.0, strategy.calculatePrice(weekBooking), 0.01);

        // 30 days rental: 25% discount, -$50 maintenance credit
        RentalBooking monthBooking = new RentalBooking(
                "B-4", testCustomer, standardVehicle, 30, 4000.0, LocalDate.of(2026, 10, 1), "Center"
        );
        // Gross = 30 * 60 = 1800. 75% = 1350.0. Net = 1350 - 50 = $1300.0
        assertEquals(1300.0, strategy.calculatePrice(monthBooking), 0.01);
    }

    @Test
    @DisplayName("LongTermSubscriptionPricingStrategy: ensures floor threshold at minimum one daily rate")
    void testLongTermStrategyMinimumFloor() {
        LongTermSubscriptionPricingStrategy strategy = new LongTermSubscriptionPricingStrategy();
        // 1 day booking: 5% discount (0.95 * 60 = 57.0), minus 50 credit = 7.0. Floor must enforce minThreshold = $60.0
        RentalBooking oneDayBooking = new RentalBooking(
                "B-5", testCustomer, standardVehicle, 1, 100.0, LocalDate.of(2026, 10, 1), "Center"
        );
        assertEquals(60.0, strategy.calculatePrice(oneDayBooking), 0.01);
    }

    @Test
    @DisplayName("DynamicDemandPricingStrategy: verifies demand surge, hazard fee and risk index integration")
    void testDynamicDemandStrategyExternalFactors() {
        fakeProvider.setSurgeMultiplier(1.40);
        fakeProvider.setWeatherWarning(true);
        fakeProvider.setRiskIndex(2.0);

        DynamicDemandPricingStrategy strategy = new DynamicDemandPricingStrategy(fakeProvider);

        // 3 days rental. Base = 3 * 60 = 180.0
        RentalBooking booking = new RentalBooking(
                "B-6", testCustomer, standardVehicle, 3, 200.0, LocalDate.of(2026, 10, 20), "Downtown"
        );
        // Surge price = 180 * 1.40 = 252.0
        // Weather surcharge = +35.0
        // Risk surcharge = 2.0 * 10 = +20.0
        // Total = 252.0 + 35.0 + 20.0 = $307.0
        assertEquals(307.0, strategy.calculatePrice(booking), 0.01);
    }

    @Test
    @DisplayName("RentalPricingContext: verifies strategy execution and runtime switching")
    void testPricingContextExecutionAndSwitching() {
        RentalPricingStrategy std = new StandardDailyPricingStrategy();
        RentalPricingStrategy lt = new LongTermSubscriptionPricingStrategy();

        RentalPricingContext context = new RentalPricingContext(std);
        assertSame(std, context.getStrategy());

        RentalBooking booking = new RentalBooking(
                "B-7", testCustomer, standardVehicle, 10, 1000.0, LocalDate.of(2026, 10, 19), "Center"
        );

        double stdResult = context.executePricing(booking);

        context.setStrategy(lt);
        assertSame(lt, context.getStrategy());
        double ltResult = context.executePricing(booking);

        assertNotEquals(stdResult, ltResult);
        assertTrue(ltResult < stdResult, "Long term strategy should offer discount over standard pricing for 10 days");
    }
}
