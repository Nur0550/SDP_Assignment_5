package com.carrental.strategy.pricing;

import com.carrental.model.RentalBooking;

import java.time.DayOfWeek;

/**
 * Standard daily rental pricing strategy.
 * Charges standard daily rates, includes 150 km/day mileage allowance,
 * applies excess kilometer surcharges, and adds weekend surcharges.
 */
public class StandardDailyPricingStrategy implements RentalPricingStrategy {

    private static final double DAILY_INCLUDED_KM = 150.0;
    private static final double EXCESS_KM_FEE = 0.25;
    private static final double WEEKEND_SURCHARGE_RATE = 0.15;

    @Override
    public double calculatePrice(RentalBooking booking) {
        double base = booking.getVehicle().getDailyRate() * booking.getRentalDays();

        double allowedKm = booking.getRentalDays() * DAILY_INCLUDED_KM;
        double extraKmFee = 0.0;
        if (booking.getEstimatedKilometers() > allowedKm) {
            extraKmFee = (booking.getEstimatedKilometers() - allowedKm) * EXCESS_KM_FEE;
        }

        boolean includesWeekend = false;
        for (int i = 0; i < booking.getRentalDays(); i++) {
            DayOfWeek dow = booking.getStartDate().plusDays(i).getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                includesWeekend = true;
                break;
            }
        }

        double weekendSurcharge = includesWeekend ? base * WEEKEND_SURCHARGE_RATE : 0.0;
        return Math.round((base + extraKmFee + weekendSurcharge) * 100.0) / 100.0;
    }
}
