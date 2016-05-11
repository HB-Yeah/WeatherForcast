package com.bin.weatherforcast.adapter;

import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;

/**
 * Created by Stranger on 2016/4/13.
 */
public class Position_List_Adapter extends BaseAdapter {
    /*
    * ListView根据JSONArray内容显示，编辑时，点击减号removeView，并记录position，确定后对应position编辑JSONArray，写入sharepreference，ListView重绘;
    * 取消，不编辑JSONArray，重绘。
    * */
    SharedPreferences sp;
    JSONArray position_array=null;
    public Position_List_Adapter(SharedPreferences sp){
        this.sp=sp;
        String s_list=sp.getString("list","");
        if (s_list.length()!=0){
            position_array=JSONArray.parseArray(s_list);
        }
    }
    @Override
    public int getCount() {
        if(position_array!=null){
            return position_array.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
