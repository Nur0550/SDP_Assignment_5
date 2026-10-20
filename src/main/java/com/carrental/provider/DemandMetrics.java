package com.carrental.provider;

public class DemandMetrics {
    private final double surgeMultiplier;
    private final boolean weatherWarning;
    private final double riskIndex;

    public DemandMetrics(double surgeMultiplier, boolean weatherWarning, double riskIndex) {
        this.surgeMultiplier = surgeMultiplier;
        this.weatherWarning = weatherWarning;
        this.riskIndex = riskIndex;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public boolean hasWeatherWarning() {
        return weatherWarning;
    }

    public double getRiskIndex() {
        return riskIndex;
    }
}
