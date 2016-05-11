package com.bin.weatherforcast.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.bin.weatherforcast.fragment.MyFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Stranger on 2016/4/16.
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {
    ArrayList<String> area_name_list;
    ArrayList<String> area_id_list;
    ArrayList<String> area_name_list_old;
    HashMap<String,MyFragment> fragment_map;
    int count;
    public MyFragmentAdapter(FragmentManager fm,ArrayList<String> name_list,ArrayList<String> id_list) {
        super(fm);
        this.area_name_list=name_list;
        this.area_id_list=id_list;
        area_name_list_old=(ArrayList<String>)this.area_name_list.clone();
        fragment_map=new HashMap<>();
        for (int i=0;i<area_id_list.size();i++){
            MyFragment myFragment = new MyFragment();
            myFragment.setArea_name(area_name_list.get(i));
            myFragment.setArea_id(this.area_id_list.get(i));
            fragment_map.put(area_name_list.get(i),myFragment);
        }
        count=area_name_list.size();
    }
    public void setNewData(ArrayList<String> name_list,ArrayList<String> id_list){
        this.area_name_list=name_list;
        this.area_id_list=id_list;
        checkDataChange();
    }
    public void checkDataChange(){
        for (int i=0;i<area_name_list_old.size();i++){
            if(!area_name_list.contains(area_name_list_old.get(i))){
                fragment_map.remove(area_name_list_old.get(i));
                area_name_list_old.remove(i);
            }
        }
        for(int i=0;i<area_name_list.size();i++){
            if(!area_name_list_old.contains(area_name_list.get(i))){
                MyFragment myFragment = new MyFragment();
                myFragment.setArea_name(area_name_list.get(i));
                myFragment.setArea_id(area_id_list.get(i));
                fragment_map.put(area_name_list.get(i),myFragment);
            }
        }
        area_name_list_old=(ArrayList<String>)area_name_list.clone();
        count=area_name_list.size();
    }
    @Override
    public Fragment getItem(int position) {


        return fragment_map.get(area_name_list.get(position));
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
