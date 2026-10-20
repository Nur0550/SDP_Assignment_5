package com.carrental;

import com.carrental.model.*;
import com.carrental.strategy.allocation.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedAllocationTest {

    static Stream<VehicleAllocationStrategy> allocationStrategiesProvider() {
        return Stream.of(
                new EconomyFirstAllocationStrategy(),
                new EcoFriendlyAllocationStrategy(),
                new LoyaltyTierAllocationStrategy()
        );
    }

    @ParameterizedTest(name = "Strategy: {0}")
    @MethodSource("allocationStrategiesProvider")
    @DisplayName("Invariant: Allocated vehicle from any strategy is available, meets seats, and respects budget")
    void testAllocationInvariants(VehicleAllocationStrategy strategy) {
        List<Vehicle> testFleet = Arrays.asList(
                new Vehicle("V-1", "Budget Compact", VehicleCategory.ECONOMY, FuelType.GASOLINE, 40.0, 6.0, 130.0, 5, 5, true),
                new Vehicle("V-2", "Green Hybrid", VehicleCategory.COMFORT, FuelType.HYBRID, 65.0, 4.0, 80.0, 7, 5, true),
                new Vehicle("V-3", "Unavailable Luxury", VehicleCategory.LUXURY, FuelType.GASOLINE, 120.0, 9.0, 200.0, 10, 5, false)
        );

        Customer customer = new Customer("C-INV", "Invariant Tester", CustomerTier.VIP, false);
        RentalCriteria criteria = new RentalCriteria(5, 80.0, customer, false);

        Optional<Vehicle> result = strategy.allocateVehicle(testFleet, criteria);

        assertTrue(result.isPresent(), "Expected vehicle allocation for strategy: " + strategy.getClass().getSimpleName());
        Vehicle vehicle = result.get();

        assertTrue(vehicle.isAvailable(), "Allocated vehicle must be available");
        assertTrue(vehicle.getSeatingCapacity() >= criteria.getMinimumSeats(), "Allocated vehicle must meet seating requirements");
        assertTrue(vehicle.getDailyRate() <= criteria.getMaxDailyBudget(), "Allocated vehicle daily rate must not exceed budget");
    }
}
