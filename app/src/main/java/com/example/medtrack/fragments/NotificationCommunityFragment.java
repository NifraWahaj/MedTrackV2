package com.example.medtrack.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medtrack.R;
import com.example.medtrack.adapters.NotificationAdapter;
import com.example.medtrack.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationCommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationCommunityFragment newInstance(String param1, String param2) {
        NotificationCommunityFragment fragment = new NotificationCommunityFragment();
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
        View view = inflater.inflate(R.layout.fragment_notification_community, container, false);

        // Find RecyclerView in the inflated layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView1);

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