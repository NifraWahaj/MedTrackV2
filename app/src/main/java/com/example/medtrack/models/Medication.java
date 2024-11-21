package com.example.medtrack.models;

public class Medication {
    private String name;
    private String frequency;
    private String reminderTime; // For single dose or interval
    private String firstIntakeDetails; // For Twice Daily
    private String secondIntakeDetails; // For Twice Daily
    private String selectedDays; // Use String for Specific Days Frequency

    private int refillAmount;
    private int refillThreshold;

    // Default constructor required for calls to DataSnapshot.getValue(Medication.class)
    public Medication() {
    }

    // Constructor for twice daily frequency
    public Medication(String name, String frequency, String firstIntakeDetails, String secondIntakeDetails, int refillAmount, int refillThreshold) {
        this.name = name;
        this.frequency = frequency;
        this.firstIntakeDetails = firstIntakeDetails;
        this.secondIntakeDetails = secondIntakeDetails;
        this.refillAmount = refillAmount;
        this.refillThreshold = refillThreshold;
    }

    // Constructor for once daily or interval without specific days
    public Medication(String name, String frequency, String reminderTime, int refillAmount, int refillThreshold) {
        this.name = name;
        this.frequency = frequency;
        this.reminderTime = reminderTime;
        this.refillAmount = refillAmount;
        this.refillThreshold = refillThreshold;
    }

    // Constructor for specific days with selected days, with an additional dummy field for distinction
    public Medication(String name, String frequency, String reminderTime, String selectedDays, int refillAmount, int refillThreshold, boolean isSpecificDays) {
        this.name = name;
        this.frequency = frequency;
        this.reminderTime = reminderTime;
        this.selectedDays = selectedDays != null ? selectedDays : ""; // Prevent null value
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

    public String getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
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