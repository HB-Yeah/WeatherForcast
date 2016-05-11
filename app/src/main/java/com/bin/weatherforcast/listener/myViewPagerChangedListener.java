package com.bin.weatherforcast.listener;

import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stranger on 2016/4/16.
 */
public class myViewPagerChangedListener implements ViewPager.OnPageChangeListener {
    ArrayList<String> area_name_list;
    TextView area_name_text_view;
    public myViewPagerChangedListener(TextView area_name_text_view,ArrayList<String> area_name_list){
        this.area_name_list=area_name_list;
        this.area_name_text_view=area_name_text_view;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        area_name_text_view.setText(area_name_list.get(position));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
