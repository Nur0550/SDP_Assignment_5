package com.carrental.strategy.pricing;

import com.carrental.model.RentalBooking;

/**
 * Strategy interface defining interchangeable rental pricing algorithms.
 */
public interface RentalPricingStrategy {

    /**
     * Calculates the total rental price for the given booking.
     *
     * @param booking the booking details including vehicle, duration, and customer
     * @return the calculated price rounded to two decimal places
     */
    double calculatePrice(RentalBooking booking);
}
