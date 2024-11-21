package com.example.medtrack.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.medtrack.fragments.CommunityFragment;
import com.example.medtrack.fragments.HomeFragment;
import com.example.medtrack.fragments.MedFragment;
import com.example.medtrack.fragments.NotificationFragment;
import com.example.medtrack.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NotificationFragment();
            case 1:
                return new MedFragment();
            case 2:
                return new HomeFragment();
            case 3:
                return new CommunityFragment();
            case 4:
                return new ProfileFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return 5;
    }
}