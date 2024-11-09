package com.example.medtrack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new MedFragment();
            case 2:
                //       return new NotificationFragment();
            case 3:
                //       return new CommunityFragment();
            default:
           //     return new ProfileFragment();
        }
        return new HomeFragment(); // TODO: idk if this is right

    }

    @Override
    public int getItemCount() {
        return 35;
    }
}