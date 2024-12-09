package com.example.medtrack.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.medtrack.R;
import com.example.medtrack.fragments.MedicationNotificationFragment;
import com.example.medtrack.models.NotificationModelMed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medicationName");
        if (medicationName != null && !medicationName.isEmpty()) {
            sendNotification(context, "Time to take your medication: " + medicationName);
        } else {
            Log.e("ReminderReceiver", "No medication name found in intent");
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "med_track_channel")
                .setSmallIcon(R.drawable.logo_launcher)
                .setContentTitle("Medication Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(new Random().nextInt(), builder.build());
    }
}

