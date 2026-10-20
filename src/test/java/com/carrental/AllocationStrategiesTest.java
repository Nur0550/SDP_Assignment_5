package com.carrental;

import com.carrental.model.*;
import com.carrental.strategy.allocation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AllocationStrategiesTest {

    private List<Vehicle> fleet;
    private Customer regularCustomer;
    private Customer vipCustomer;
    private Customer ecoCustomer;

    @BeforeEach
    void setUp() {
        fleet = Arrays.asList(
                new Vehicle("V-1", "Compact Gas", VehicleCategory.ECONOMY, FuelType.GASOLINE, 35.0, 6.0, 140.0, 4, 5, true),
                new Vehicle("V-2", "Budget Diesel", VehicleCategory.ECONOMY, FuelType.DIESEL, 38.0, 5.0, 130.0, 5, 5, true),
                new Vehicle("V-3", "Eco Hybrid", VehicleCategory.COMFORT, FuelType.HYBRID, 55.0, 4.0, 70.0, 7, 5, true),
                new Vehicle("V-4", "Pure Electric EV", VehicleCategory.COMFORT, FuelType.ELECTRIC, 70.0, 0.0, 0.0, 8, 5, true),
                new Vehicle("V-5", "Luxury Sedan", VehicleCategory.LUXURY, FuelType.GASOLINE, 150.0, 9.5, 210.0, 10, 5, true),
                new Vehicle("V-6", "Family SUV (Unavailable)", VehicleCategory.SUV, FuelType.DIESEL, 80.0, 7.5, 175.0, 8, 7, false)
        );

        regularCustomer = new Customer("C-1", "Regular Joe", CustomerTier.REGULAR, false);
        vipCustomer = new Customer("C-2", "VIP Victor", CustomerTier.VIP, false);
        ecoCustomer = new Customer("C-3", "Green Greta", CustomerTier.SILVER, true);
    }

    @Test
    @DisplayName("EconomyFirstAllocationStrategy: selects vehicle with lowest daily rate and fuel usage")
    void testEconomyFirstAllocation() {
        EconomyFirstAllocationStrategy strategy = new EconomyFirstAllocationStrategy();
        RentalCriteria criteria = new RentalCriteria(5, 100.0, regularCustomer, false);

        Optional<Vehicle> allocated = strategy.allocateVehicle(fleet, criteria);

        assertTrue(allocated.isPresent());
        assertEquals("V-1", allocated.get().getId());
        assertEquals(35.0, allocated.get().getDailyRate(), 0.01);
    }

    @Test
    @DisplayName("EcoFriendlyAllocationStrategy: prioritizes electric EV over hybrid and combustion engines")
    void testEcoFriendlyAllocation() {
        EcoFriendlyAllocationStrategy strategy = new EcoFriendlyAllocationStrategy();
        RentalCriteria criteria = new RentalCriteria(5, 100.0, ecoCustomer, true);

        Optional<Vehicle> allocated = strategy.allocateVehicle(fleet, criteria);

        assertTrue(allocated.isPresent());
        assertEquals("V-4", allocated.get().getId());
        assertEquals(FuelType.ELECTRIC, allocated.get().getFuelType());
        assertEquals(0.0, allocated.get().getCo2EmissionGPerKm(), 0.01);
    }

    @Test
    @DisplayName("LoyaltyTierAllocationStrategy: upgrades VIP customer to highest comfort score and category")
    void testLoyaltyTierAllocationVIP() {
        LoyaltyTierAllocationStrategy strategy = new LoyaltyTierAllocationStrategy();
        // VIP customer with budget $200
        RentalCriteria vipCriteria = new RentalCriteria(5, 200.0, vipCustomer, false);

        Optional<Vehicle> allocated = strategy.allocateVehicle(fleet, vipCriteria);

        assertTrue(allocated.isPresent());
        assertEquals("V-5", allocated.get().getId());
        assertEquals(VehicleCategory.LUXURY, allocated.get().getCategory());
        assertEquals(10, allocated.get().getComfortScore());
    }

    @Test
    @DisplayName("VehicleAllocationContext: executes strategy and respects runtime strategy updates")
    void testVehicleAllocationContext() {
        RentalCriteria criteria = new RentalCriteria(5, 100.0, regularCustomer, false);

        VehicleAllocationContext context = new VehicleAllocationContext(new EconomyFirstAllocationStrategy());
        Vehicle vehicle1 = context.executeAllocation(fleet, criteria).orElseThrow();
        assertEquals("V-1", vehicle1.getId()); // Lowest price

        context.setStrategy(new EcoFriendlyAllocationStrategy());
        Vehicle vehicle2 = context.executeAllocation(fleet, criteria).orElseThrow();
        assertEquals("V-4", vehicle2.getId()); // Electric EV
    }
}
