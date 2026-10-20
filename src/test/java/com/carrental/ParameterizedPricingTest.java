package com.carrental;

import com.carrental.model.*;
import com.carrental.provider.FakeMarketDemandProvider;
import com.carrental.strategy.pricing.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedPricingTest {

    static Stream<RentalPricingStrategy> pricingStrategiesProvider() {
        FakeMarketDemandProvider fakeProvider = new FakeMarketDemandProvider(1.10, false, 0.5);
        return Stream.of(
                new StandardDailyPricingStrategy(),
                new LongTermSubscriptionPricingStrategy(),
                new DynamicDemandPricingStrategy(fakeProvider)
        );
    }

    @ParameterizedTest(name = "Strategy: {0}")
    @MethodSource("pricingStrategiesProvider")
    @DisplayName("Invariant: Price is strictly positive and non-zero across all pricing strategies")
    void testPriceIsNeverNegativeOrZero(RentalPricingStrategy strategy) {
        Customer customer = new Customer("C-1", "Test Customer", CustomerTier.REGULAR, false);
        Vehicle vehicle = new Vehicle("V-1", "Sedan", VehicleCategory.ECONOMY, FuelType.GASOLINE, 40.0, 6.0, 130.0, 5, 5, true);

        RentalBooking booking = new RentalBooking(
                "B-PARAM", customer, vehicle, 3, 250.0, LocalDate.of(2026, 11, 2), "Downtown"
        );

        double calculatedPrice = strategy.calculatePrice(booking);

        assertTrue(calculatedPrice > 0.0,
                () -> "Calculated price must be strictly positive for strategy: " + strategy.getClass().getSimpleName()
                        + " but got: " + calculatedPrice);
    }

    @ParameterizedTest(name = "Strategy: {0}")
    @MethodSource("pricingStrategiesProvider")
    @DisplayName("Invariant: Price increases or stays equal with extended duration across all strategies")
    void testPriceMonotonicallyScalesWithDuration(RentalPricingStrategy strategy) {
        Customer customer = new Customer("C-2", "Test Customer 2", CustomerTier.REGULAR, false);
        Vehicle vehicle = new Vehicle("V-2", "SUV", VehicleCategory.SUV, FuelType.DIESEL, 90.0, 8.0, 180.0, 8, 7, true);

        RentalBooking shortBooking = new RentalBooking(
                "B-SHORT", customer, vehicle, 2, 200.0, LocalDate.of(2026, 11, 2), "Downtown"
        );
        RentalBooking longerBooking = new RentalBooking(
                "B-LONG", customer, vehicle, 8, 800.0, LocalDate.of(2026, 11, 2), "Downtown"
        );

        double shortPrice = strategy.calculatePrice(shortBooking);
        double longPrice = strategy.calculatePrice(longerBooking);

        assertTrue(longPrice > shortPrice,
                () -> "Longer rental price (" + longPrice + ") must exceed short rental price (" + shortPrice
                        + ") for strategy: " + strategy.getClass().getSimpleName());
    }
}
