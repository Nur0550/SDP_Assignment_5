package com.carrental;

import com.carrental.model.*;
import com.carrental.provider.FakeMarketDemandProvider;
import com.carrental.service.RentalPriceCalculator;
import com.carrental.service.RentalPricingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RentalPricingRefactoringTest {

    private FakeMarketDemandProvider fakeDemandProvider;
    private RentalPriceCalculator calculator;
    private Vehicle economyCar;
    private Customer regularCustomer;

    @BeforeEach
    void setUp() {
        fakeDemandProvider = new FakeMarketDemandProvider(1.20, false, 0.5);
        calculator = new RentalPriceCalculator(fakeDemandProvider);

        economyCar = new Vehicle(
                "V-101", "Toyota Corolla", VehicleCategory.ECONOMY, FuelType.GASOLINE,
                50.0, 6.5, 120.0, 6, 5, true
        );

        regularCustomer = new Customer("C-001", "Alice Smith", CustomerTier.REGULAR, false);
    }

    @Test
    @DisplayName("Standard pricing: includes base rate, mileage excess surcharge, and weekend fee")
    void testStandardPricingCalculations() {
        // Start date on Monday (2026-10-19), 3 days (Mon, Tue, Wed) -> no weekend
        // 3 days * 50.0 = 150.0. Allowance = 3 * 150 = 450 km. Actual = 500 km -> 50 km excess * 0.25 = 12.50. Total = 162.50
        RentalBooking weekdayBooking = new RentalBooking(
                "B-1", regularCustomer, economyCar, 3, 500.0, LocalDate.of(2026, 10, 19), "City Center"
        );
        double weekdayPrice = calculator.calculatePrice(weekdayBooking, RentalPricingType.STANDARD);
        assertEquals(162.50, weekdayPrice, 0.01);

        // Start date on Friday (2026-10-23), 3 days (Fri, Sat, Sun) -> includes weekend!
        // Base = 150.0. Weekend surcharge = 150.0 * 0.15 = 22.50. Kilometers = 300 (under 450). Total = 172.50
        RentalBooking weekendBooking = new RentalBooking(
                "B-2", regularCustomer, economyCar, 3, 300.0, LocalDate.of(2026, 10, 23), "City Center"
        );
        double weekendPrice = calculator.calculatePrice(weekendBooking, RentalPricingType.STANDARD);
        assertEquals(172.50, weekendPrice, 0.01);
    }

    @Test
    @DisplayName("Long-term pricing: applies tiered volume discount and maintenance credit")
    void testLongTermPricingCalculations() {
        // 10 days rental (>= 7 days -> 15% discount)
        // Gross = 10 * 50 = 500.0. Discounted (85%) = 425.0. Maintenance credit = -50.0 -> Net = 375.00
        RentalBooking tenDaysBooking = new RentalBooking(
                "B-3", regularCustomer, economyCar, 10, 2000.0, LocalDate.of(2026, 10, 19), "Suburbs"
        );
        double tenDaysPrice = calculator.calculatePrice(tenDaysBooking, RentalPricingType.LONG_TERM);
        assertEquals(375.00, tenDaysPrice, 0.01);

        // 30 days rental (>= 30 days -> 25% discount)
        // Gross = 30 * 50 = 1500.0. Discounted (75%) = 1125.0. Maintenance credit = -50.0 -> Net = 1075.00
        RentalBooking monthlyBooking = new RentalBooking(
                "B-4", regularCustomer, economyCar, 30, 5000.0, LocalDate.of(2026, 10, 19), "Airport"
        );
        double monthlyPrice = calculator.calculatePrice(monthlyBooking, RentalPricingType.LONG_TERM);
        assertEquals(1075.00, monthlyPrice, 0.01);
    }

    @Test
    @DisplayName("Dynamic demand pricing: applies external surge multiplier and hazard fees")
    void testDynamicDemandPricingCalculations() {
        // 2 days rental. Base = 2 * 50 = 100.0.
        // Fake metrics: surge = 1.30, weatherWarning = true (+35.0), riskIndex = 1.0 (+10.0)
        fakeDemandProvider.setSurgeMultiplier(1.30);
        fakeDemandProvider.setWeatherWarning(true);
        fakeDemandProvider.setRiskIndex(1.0);

        RentalBooking surgeBooking = new RentalBooking(
                "B-5", regularCustomer, economyCar, 2, 200.0, LocalDate.of(2026, 10, 20), "Downtown"
        );
        // Expected: 100 * 1.30 = 130.0 + 35.0 (hazard) + 10.0 (risk) = 175.00
        double dynamicPrice = calculator.calculatePrice(surgeBooking, RentalPricingType.DYNAMIC_DEMAND);
        assertEquals(175.00, dynamicPrice, 0.01);
    }
}
