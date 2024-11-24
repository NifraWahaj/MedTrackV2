package com.example.medtrack.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;
import com.example.medtrack.adapters.MedicationListAdapter;
import com.example.medtrack.models.Medication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedFragment extends Fragment {

    private RecyclerView medicationRecyclerView;
    private MedicationListAdapter medicationAdapter;
    private List<Medication> medicationList = new ArrayList<>();
    private static final String TAG = "MedFragment";

    public MedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_med, container, false);

        // Initialize RecyclerView
        medicationRecyclerView = view.findViewById(R.id.medicationRecyclerView);
        medicationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicationAdapter = new MedicationListAdapter(getContext(), medicationList);
        medicationRecyclerView.setAdapter(medicationAdapter);

        // Initialize FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "FAB Clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), AddMedActivity.class);
            startActivity(intent);
        });

        // Fetch medications from Firebase
        fetchMedicationsFromFirebase();

        return view;
    }

    private void fetchMedicationsFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference medsRef = FirebaseDatabase.getInstance()
                .getReference("medications")
                .child(userId);

        medsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);
                    if (medication != null) {
                        medicationList.add(medication);
                    }
                }
                medicationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to fetch medications.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
