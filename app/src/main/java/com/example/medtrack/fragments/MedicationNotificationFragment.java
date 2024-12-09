package com.example.medtrack.fragments;

import android.os.Bundle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.adapters.MedNotificationAdapter;
import com.example.medtrack.adapters.NotificationAdapter;
import com.example.medtrack.models.NotificationModelMed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicationNotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedNotificationAdapter adapter; // Corrected the adapter type
    private List<NotificationModelMed> notifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_medication, container, false);

        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch or create notifications
        notifications = fetchMedicationNotifications();
        Collections.sort(notifications, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp())); // Sort by timestamp

        adapter = new MedNotificationAdapter(notifications); // Use MedNotificationAdapter
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<NotificationModelMed> fetchMedicationNotifications() {
        List<NotificationModelMed> notifications = new ArrayList<>();

        // Example dummy notifications
        long currentTime = System.currentTimeMillis();
        notifications.add(new NotificationModelMed("Paracetamol", "Time to take your medication", currentTime - 60000)); // 1 minute ago
        notifications.add(new NotificationModelMed("Azithromycin", "Time to take your medication", currentTime - 300000)); // 5 minutes ago
        notifications.add(new NotificationModelMed("Chloramphenicol", "Time to take your medication", currentTime - 3600000)); // 1 hour ago

        return notifications;
    }
}
