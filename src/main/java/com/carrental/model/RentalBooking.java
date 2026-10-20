package com.carrental.model;

import java.time.LocalDate;

public class RentalBooking {
    private final String bookingId;
    private final Customer customer;
    private final Vehicle vehicle;
    private final int rentalDays;
    private final double estimatedKilometers;
    private final LocalDate startDate;
    private final String location;

    public RentalBooking(String bookingId, Customer customer, Vehicle vehicle,
                         int rentalDays, double estimatedKilometers, LocalDate startDate, String location) {
        if (rentalDays <= 0) {
            throw new IllegalArgumentException("Rental days must be greater than zero");
        }
        if (estimatedKilometers < 0) {
            throw new IllegalArgumentException("Kilometers cannot be negative");
        }
        this.bookingId = bookingId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalDays = rentalDays;
        this.estimatedKilometers = estimatedKilometers;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.location = location != null ? location : "Standard-City";
    }

    public String getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public double getEstimatedKilometers() {
        return estimatedKilometers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getLocation() {
        return location;
    }
}
