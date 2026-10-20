package com.carrental.strategy.allocation;

import com.carrental.model.RentalCriteria;
import com.carrental.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface defining interchangeable fleet allocation algorithms.
 */
public interface VehicleAllocationStrategy {

    /**
     * Selects the most appropriate vehicle from available fleet according to specific algorithmic rules.
     *
     * @param fleet the complete or candidate fleet of vehicles
     * @param criteria customer and rental constraints
     * @return an Optional containing the matched vehicle, or empty if no vehicle satisfies criteria
     */
    Optional<Vehicle> allocateVehicle(List<Vehicle> fleet, RentalCriteria criteria);
}
