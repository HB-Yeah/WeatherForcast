package com.bin.weatherforcast.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.listener.LocalInfoSetListener;

/**
 * Created by Stranger on 2016/5/9.
 */
public class LocalInfoBeanManager {
    LocalInfoSetListener listener;
    LocalInfoBean bean;
    public void setListener(LocalInfoSetListener l){
        listener=l;
    }
    public LocalInfoBeanManager(String six_day,String now,String three_hours,String life_suggestion){
        bean=new LocalInfoBean();
        setNow(now);
        if(listener!=null)
            listener.afterSetDone();
    }
    public LocalInfoBean getBean(){
        return bean;
    }

    private  void setNow(String s_now){
        JSONObject now = JSONObject.parseObject(s_now);
        String weather_pic_url = now.getString("weather_pic");
        String s_pic_num = weather_pic_url.substring(weather_pic_url.length() - 6, weather_pic_url.length() - 4);
        int pic_num = Integer.parseInt(s_pic_num);
        StringBuffer time_status = new StringBuffer();
        if (weather_pic_url.contains("night")) {
            time_status.append("n");
        } else {
            time_status.append("d");
        }
    }

}
