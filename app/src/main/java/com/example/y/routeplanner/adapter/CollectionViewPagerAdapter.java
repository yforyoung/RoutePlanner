package com.example.y.routeplanner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.example.y.routeplanner.fragment.CollectionPointFragment;
import com.example.y.routeplanner.fragment.CollectionRouteFragment;




public class CollectionViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments=new Fragment[]{new CollectionRouteFragment(),new CollectionPointFragment()};
    public CollectionViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 2;
    }


}
