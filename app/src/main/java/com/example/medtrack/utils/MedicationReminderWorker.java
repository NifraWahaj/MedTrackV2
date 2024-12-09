package com.example.medtrack.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MedicationReminderWorker extends Worker {
    public static final String TAG = "MedicationReminderWorker";

    public MedicationReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get medication details from inputData
        String medicationName = getInputData().getString("medicationName");

        Log.d(TAG, "Sending notification for medication: " + medicationName);

        // Show notification
        showNotification(medicationName);

        return Result.success();
    }

    private void showNotification(String medicationName) {
        String channelId = "medication_reminder_channel";
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel if running on Android O or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Medication Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medication Reminder")
                .setContentText("Time to take your medication: " + medicationName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Send the notification
        notificationManager.notify(medicationName.hashCode(), builder.build());
    }
}
