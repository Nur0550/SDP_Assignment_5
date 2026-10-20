package com.carrental.strategy.allocation;

import com.carrental.model.FuelType;
import com.carrental.model.RentalCriteria;
import com.carrental.model.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Eco-friendly vehicle allocation strategy.
 * Minimizes carbon footprint by prioritizing zero-emission electric vehicles (EV),
 * followed by hybrids, with lowest CO2 emissions and fuel consumption.
 */
public class EcoFriendlyAllocationStrategy implements VehicleAllocationStrategy {

    @Override
    public Optional<Vehicle> allocateVehicle(List<Vehicle> fleet, RentalCriteria criteria) {
        if (fleet == null || criteria == null) {
            return Optional.empty();
        }

        return fleet.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> v.getSeatingCapacity() >= criteria.getMinimumSeats())
                .filter(v -> v.getDailyRate() <= criteria.getMaxDailyBudget())
                .filter(v -> !criteria.isEcoFriendlyOnly() || v.getFuelType() == FuelType.ELECTRIC || v.getFuelType() == FuelType.HYBRID)
                .min(Comparator
                        .comparingInt(this::getFuelTypePriority)
                        .thenComparingDouble(Vehicle::getCo2EmissionGPerKm)
                        .thenComparingDouble(Vehicle::getFuelConsumptionPer100Km));
    }

    private int getFuelTypePriority(Vehicle vehicle) {
        switch (vehicle.getFuelType()) {
            case ELECTRIC:
                return 1;
            case HYBRID:
                return 2;
            case GASOLINE:
                return 3;
            case DIESEL:
                return 4;
            default:
                return 5;
        }
    }
}
