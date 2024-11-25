package com.example.medtrack.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medtrack.R;
import com.example.medtrack.adapters.*;
import com.example.medtrack.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

public class NotificationMedicationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NotificationMedicationFragment() {
        // Required empty public constructor
    }

    public static NotificationMedicationFragment newInstance(String param1, String param2) {
        NotificationMedicationFragment fragment = new NotificationMedicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_medication, container, false);

        // Find RecyclerView in the inflated layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView2);

        // Create dummy notifications
        List<NotificationModel> notifications = new ArrayList<>();
        notifications.add(new NotificationModel("John23", "Lorem ipsum dolor sit amet.", "1m ago"));
        notifications.add(new NotificationModel("Jane1", "Consectetur adipiscing elit.", "5m ago"));
        notifications.add(new NotificationModel("Doe45", "Sed do eiusmod tempor incididunt.", "10m ago"));

        // Set up the adapter and RecyclerView
        NotificationAdapter adapter = new NotificationAdapter(notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
