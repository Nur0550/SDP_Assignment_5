package com.carrental.selector;

import com.carrental.model.CustomerTier;
import com.carrental.model.RentalCriteria;
import com.carrental.strategy.allocation.EcoFriendlyAllocationStrategy;
import com.carrental.strategy.allocation.EconomyFirstAllocationStrategy;
import com.carrental.strategy.allocation.LoyaltyTierAllocationStrategy;
import com.carrental.strategy.allocation.VehicleAllocationStrategy;

/**
 * Selector responsible for automatically picking the vehicle allocation strategy
 * based on customer environmental preference, loyalty tier, and booking criteria.
 * Conditional logic (if / switch) is explicitly permitted here.
 */
public class VehicleAllocationStrategySelector {

    private final VehicleAllocationStrategy economyFirstStrategy;
    private final VehicleAllocationStrategy ecoFriendlyStrategy;
    private final VehicleAllocationStrategy loyaltyTierStrategy;

    public VehicleAllocationStrategySelector() {
        this.economyFirstStrategy = new EconomyFirstAllocationStrategy();
        this.ecoFriendlyStrategy = new EcoFriendlyAllocationStrategy();
        this.loyaltyTierStrategy = new LoyaltyTierAllocationStrategy();
    }

    public VehicleAllocationStrategy selectStrategy(RentalCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Criteria cannot be null");
        }

        // Rule 1: Customer explicitly requested eco-friendly vehicle or has registered green preference
        boolean customerEcoPref = criteria.getCustomer() != null && criteria.getCustomer().hasEcoFriendlyPreference();
        if (criteria.isEcoFriendlyOnly() || customerEcoPref) {
            return ecoFriendlyStrategy;
        }

        // Rule 2: High loyalty tiers (VIP and GOLD) receive priority tier-based upgrade allocation
        CustomerTier tier = criteria.getCustomer() != null ? criteria.getCustomer().getLoyaltyTier() : CustomerTier.REGULAR;
        if (tier == CustomerTier.VIP || tier == CustomerTier.GOLD) {
            return loyaltyTierStrategy;
        }

        // Rule 3: Standard customers default to cost-saving economy allocation
        return economyFirstStrategy;
    }

    public VehicleAllocationStrategy getEconomyFirstStrategy() {
        return economyFirstStrategy;
    }

    public VehicleAllocationStrategy getEcoFriendlyStrategy() {
        return ecoFriendlyStrategy;
    }

    public VehicleAllocationStrategy getLoyaltyTierStrategy() {
        return loyaltyTierStrategy;
    }
}
