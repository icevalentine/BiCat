package com.banledcamung.bicat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    public SampleFragment fragment1, fragment2, fragment3;
    public CustomFragment fragment4;
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public PagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        fragment1 = new SampleFragment(0);
        fragment2 = new SampleFragment(1);
        fragment3 = new SampleFragment(2);
        fragment4 = new CustomFragment();
        switch (position){
            case 0:
                return fragment1;
            case 1:
                return  fragment2;
            case 2:
                return fragment3;
            case 3:
                return fragment4;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
