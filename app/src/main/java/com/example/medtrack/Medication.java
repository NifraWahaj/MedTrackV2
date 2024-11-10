package com.example.medtrack;

public class Medication {
    public String name;
    public String unit;
    public String frequency;
    public String reminderTime;
    public int refillAmount;
    public int refillThreshold;

    public Medication() {
        // Default constructor required for calls to DataSnapshot.getValue(Medication.class)
    }

    public Medication(String name, String unit, String frequency, String reminderTime, int refillAmount, int refillThreshold) {
        this.name = name;
        this.unit = unit;
        this.frequency = frequency;
        this.reminderTime = reminderTime;
        this.refillAmount = refillAmount;
        this.refillThreshold = refillThreshold;
    }
}

