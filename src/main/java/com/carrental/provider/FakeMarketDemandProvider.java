package com.carrental.provider;

import java.time.LocalDate;

public class FakeMarketDemandProvider implements MarketDemandProvider {
    private double surgeMultiplier;
    private boolean weatherWarning;
    private double riskIndex;

    public FakeMarketDemandProvider(double surgeMultiplier, boolean weatherWarning, double riskIndex) {
        this.surgeMultiplier = surgeMultiplier;
        this.weatherWarning = weatherWarning;
        this.riskIndex = riskIndex;
    }

    public void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public void setWeatherWarning(boolean weatherWarning) {
        this.weatherWarning = weatherWarning;
    }

    public void setRiskIndex(double riskIndex) {
        this.riskIndex = riskIndex;
    }

    @Override
    public DemandMetrics getDemandMetrics(String location, LocalDate rentalDate) {
        return new DemandMetrics(surgeMultiplier, weatherWarning, riskIndex);
    }
}
