package com.carrental.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

/**
 * Simulated implementation of external market demand and weather data provider.
 * Mimics an external REST service or telematics feed by calculating realistic
 * demand multipliers and weather alerts based on calendar and regional parameters.
 */
public class SimulatedMarketDemandProvider implements MarketDemandProvider {

    @Override
    public DemandMetrics getDemandMetrics(String location, LocalDate rentalDate) {
        LocalDate date = rentalDate != null ? rentalDate : LocalDate.now();

        // 1. Seasonal and weekend surge calculation
        double surgeMultiplier = 1.0;
        Month month = date.getMonth();
        if (month == Month.JULY || month == Month.AUGUST || month == Month.DECEMBER) {
            surgeMultiplier += 0.25; // High summer/holiday season surge
        }

        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.FRIDAY || dow == DayOfWeek.SATURDAY) {
            surgeMultiplier += 0.15; // Weekend getaway surge
        }

        // 2. Weather warning simulation (e.g. winter storms in Jan/Feb or rainy autumn in Nov)
        boolean weatherWarning = (month == Month.JANUARY || month == Month.FEBRUARY || month == Month.NOVEMBER);

        // 3. Regional risk index simulation based on location
        double riskIndex = 0.5;
        if (location != null && (location.equalsIgnoreCase("Mountain-Pass") || location.equalsIgnoreCase("High-Altitude"))) {
            riskIndex = 1.2;
        } else if (location != null && location.equalsIgnoreCase("Airport")) {
            riskIndex = 0.3;
        }

        return new DemandMetrics(Math.round(surgeMultiplier * 100.0) / 100.0, weatherWarning, riskIndex);
    }
}
