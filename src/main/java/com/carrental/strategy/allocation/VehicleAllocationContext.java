package com.carrental.strategy.allocation;

import com.carrental.model.RentalCriteria;
import com.carrental.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Context class for the vehicle allocation strategy family.
 * Holds a reference to a VehicleAllocationStrategy and delegates vehicle selection to it.
 * Strictly adheres to the Strategy pattern by omitting any conditional logic
 * (no if, switch, or instanceof checks).
 */
public class VehicleAllocationContext {

    private VehicleAllocationStrategy strategy;

    public VehicleAllocationContext(VehicleAllocationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Initial allocation strategy cannot be null");
        }
        this.strategy = strategy;
    }

    public void setStrategy(VehicleAllocationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Allocation strategy cannot be set to null");
        }
        this.strategy = strategy;
    }

    public VehicleAllocationStrategy getStrategy() {
        return strategy;
    }

    public Optional<Vehicle> executeAllocation(List<Vehicle> fleet, RentalCriteria criteria) {
        return strategy.allocateVehicle(fleet, criteria);
    }
}
