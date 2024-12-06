package com.example.medtrack.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ValidationUtils {
    public static boolean isNonEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidDateRange(String startDate, String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            return start != null && end != null && !start.after(end);
        } catch (Exception e) {
            return false;
        }
    }
}
