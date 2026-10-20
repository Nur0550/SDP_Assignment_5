package com.carrental.provider;

import java.time.LocalDate;

public interface MarketDemandProvider {
    DemandMetrics getDemandMetrics(String location, LocalDate rentalDate);
}
