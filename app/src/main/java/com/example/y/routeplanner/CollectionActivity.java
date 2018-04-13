package com.example.y.routeplanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.view.MenuItem;




import com.example.y.routeplanner.adapter.CollectionViewPagerAdapter;



public class CollectionActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener {
    private ViewPager collectionViewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        collectionViewPager=findViewById(R.id.collection_container);
        bottomNavigationView=findViewById(R.id.coll);

        FragmentManager manager = getSupportFragmentManager();
        CollectionViewPagerAdapter adapter = new CollectionViewPagerAdapter(manager);
        collectionViewPager.setAdapter(adapter);
        collectionViewPager.addOnPageChangeListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.collection_route:
                collectionViewPager.setCurrentItem(0);
                return true;
            case R.id.collection_point:
                collectionViewPager.setCurrentItem(1);
                return true;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.collection_route);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.collection_point);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
