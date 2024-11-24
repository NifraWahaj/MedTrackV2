package com.example.medtrack.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.medtrack.R;

public class NotificationWorker extends Worker {
    private static final String CHANNEL_ID = "medtrack_channel";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        String medName = getInputData().getString("medName");
        String reminderTime = getInputData().getString("reminderTime");

        Log.d("NotificationWorker", "Triggered for: " + medName + " at " + reminderTime);

        // Send the notification
        sendNotification("Medication Reminder", "Time to take " + medName);

        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Medication Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for medication reminders");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
