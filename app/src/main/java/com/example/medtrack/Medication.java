package com.example.medtrack;

public class Medication {
    private String name;
    private String unit;
    private String frequency;
    private String reminderTime; // For single dose
    private String firstIntakeDetails; // For Twice Daily
    private String secondIntakeDetails; // For Twice Daily
    private int refillAmount;
    private int refillThreshold;

    // Default constructor required for calls to DataSnapshot.getValue(Medication.class)
    public Medication() {
    }

    // For single dose medication
    public Medication(String name, String unit, String frequency, String reminderTime, int refillAmount, int refillThreshold) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.reminderTime = reminderTime;
        this.refillAmount = refillAmount;
        this.refillThreshold = refillThreshold;
    }

    // For twice daily medication
    public Medication(String name, String unit, String frequency, String firstIntakeDetails, String secondIntakeDetails, int refillAmount, int refillThreshold) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.firstIntakeDetails = firstIntakeDetails;
        this.secondIntakeDetails = secondIntakeDetails;
        this.refillAmount = refillAmount;
        this.refillThreshold = refillThreshold;
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getFirstIntakeDetails() {
        return firstIntakeDetails;
    }

    public void setFirstIntakeDetails(String firstIntakeDetails) {
        this.firstIntakeDetails = firstIntakeDetails;
    }

    public String getSecondIntakeDetails() {
        return secondIntakeDetails;
    }

    public void setSecondIntakeDetails(String secondIntakeDetails) {
        this.secondIntakeDetails = secondIntakeDetails;
    }

    public int getRefillAmount() {
        return refillAmount;
    }

    public void setRefillAmount(int refillAmount) {
        this.refillAmount = refillAmount;
    }

    public int getRefillThreshold() {
        return refillThreshold;
    }

    public void setRefillThreshold(int refillThreshold) {
        this.refillThreshold = refillThreshold;
    }
}
