package com.example.ac_instaclone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SocialMediaActivityAdapter extends FragmentStateAdapter {
    public SocialMediaActivityAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 :
                return new UsersTab();
            case 1 :
                return new ProfileTab();
            case 2 :
                return new SharePictureTab();
                default:
                    return new UsersTab();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
