package com.carrental.strategy.allocation;

import com.carrental.model.RentalCriteria;
import com.carrental.model.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Economy-first vehicle allocation strategy.
 * Minimizes customer cost by prioritizing the lowest daily rental rate
 * followed by the lowest fuel consumption among available vehicles.
 */
public class EconomyFirstAllocationStrategy implements VehicleAllocationStrategy {

    @Override
    public Optional<Vehicle> allocateVehicle(List<Vehicle> fleet, RentalCriteria criteria) {
        if (fleet == null || criteria == null) {
            return Optional.empty();
        }

        return fleet.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> v.getSeatingCapacity() >= criteria.getMinimumSeats())
                .filter(v -> v.getDailyRate() <= criteria.getMaxDailyBudget())
                .min(Comparator
                        .comparingDouble(Vehicle::getDailyRate)
                        .thenComparingDouble(Vehicle::getFuelConsumptionPer100Km));
    }
}
