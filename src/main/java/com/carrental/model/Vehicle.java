package com.carrental.model;

public class Vehicle {
    private final String id;
    private final String modelName;
    private final VehicleCategory category;
    private final FuelType fuelType;
    private final double dailyRate;
    private final double fuelConsumptionPer100Km;
    private final double co2EmissionGPerKm;
    private final int comfortScore; // 1 to 10
    private final int seatingCapacity;
    private boolean available;

    public Vehicle(String id, String modelName, VehicleCategory category, FuelType fuelType,
                   double dailyRate, double fuelConsumptionPer100Km, double co2EmissionGPerKm,
                   int comfortScore, int seatingCapacity, boolean available) {
        this.id = id;
        this.modelName = modelName;
        this.category = category;
        this.fuelType = fuelType;
        this.dailyRate = dailyRate;
        this.fuelConsumptionPer100Km = fuelConsumptionPer100Km;
        this.co2EmissionGPerKm = co2EmissionGPerKm;
        this.comfortScore = comfortScore;
        this.seatingCapacity = seatingCapacity;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public double getFuelConsumptionPer100Km() {
        return fuelConsumptionPer100Km;
    }

    public double getCo2EmissionGPerKm() {
        return co2EmissionGPerKm;
    }

    public int getComfortScore() {
        return comfortScore;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, $%.2f/day, score=%d, seats=%d)",
                modelName, category, fuelType, dailyRate, comfortScore, seatingCapacity);
    }
}
