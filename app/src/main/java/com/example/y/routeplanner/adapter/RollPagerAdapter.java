package com.example.y.routeplanner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.y.routeplanner.R;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

public class RollPagerAdapter extends StaticPagerAdapter {
    private int img[] = {R.drawable.roll_3,R.drawable.roll_1,
            R.drawable.roll_2};

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setImageResource(img[position]);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
