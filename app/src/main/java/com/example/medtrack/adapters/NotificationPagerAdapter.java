package com.example.medtrack.adapters;

import android.app.Notification;

import com.example.medtrack.fragments.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;



public class NotificationPagerAdapter extends FragmentStateAdapter {

    public NotificationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // Community Tab
                return new NotificationCommunityFragment();
            case 1: // Medication Tab
                return new MedicationNotificationFragment();
            default:
                return new NotificationCommunityFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}