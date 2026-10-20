package com.carrental.strategy.allocation;

import com.carrental.model.CustomerTier;
import com.carrental.model.RentalCriteria;
import com.carrental.model.Vehicle;
import com.carrental.model.VehicleCategory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Loyalty-tier vehicle allocation strategy.
 * Maximizes customer experience and comfort by awarding VIP and Gold tier members
 * complimentary vehicle category upgrades, selecting highest comfort score vehicles.
 */
public class LoyaltyTierAllocationStrategy implements VehicleAllocationStrategy {

    @Override
    public Optional<Vehicle> allocateVehicle(List<Vehicle> fleet, RentalCriteria criteria) {
        if (fleet == null || criteria == null) {
            return Optional.empty();
        }

        CustomerTier tier = criteria.getCustomer() != null
                ? criteria.getCustomer().getLoyaltyTier()
                : CustomerTier.REGULAR;

        boolean isVipOrGold = (tier == CustomerTier.VIP || tier == CustomerTier.GOLD);

        return fleet.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> v.getSeatingCapacity() >= criteria.getMinimumSeats())
                .filter(v -> v.getDailyRate() <= criteria.getMaxDailyBudget())
                .max(Comparator
                        .comparingInt((Vehicle v) -> isVipOrGold ? getCategoryRank(v.getCategory()) : 0)
                        .thenComparingInt(Vehicle::getComfortScore)
                        .thenComparingDouble(v -> -v.getDailyRate()));
    }

    private int getCategoryRank(VehicleCategory category) {
        switch (category) {
            case LUXURY:
                return 4;
            case SUV:
                return 3;
            case COMFORT:
                return 2;
            case ECONOMY:
            default:
                return 1;
        }
    }
}
