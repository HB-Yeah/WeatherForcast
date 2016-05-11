package com.bin.weatherforcast.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.R;
import com.bin.weatherforcast.receiver.WeatherInfoRefreshDone;
import com.bin.weatherforcast.service.WeatherInfoRefresh;
import com.bin.weatherforcast.utils.Weather_contants;
import com.bin.weatherforcast.utils.LocalInfoBean;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stranger on 2016/4/16.
 */
public class MyFragment extends Fragment {
    private String area_name;
    private String area_id;
    public  void setArea_name(String area_name){
        this.area_name=area_name;
    }
    public void setArea_id(String area_id){
        this.area_id=area_id;
    }
    WeatherInfoRefreshDone myReceiver;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    doRefresh();
                    if(mSwipeLayout.isRefreshing()){
                        mSwipeLayout.setRefreshing(false);
                    }
                    break;
                case 1:
                    doRefresh();
                    break;
            }

        }
    };

//    public static MyFragment newInstance(String area_name, String area_id) {
//
//        Bundle args = new Bundle();
//        args.putString("area_name", area_name);
//        args.putString("area_id", area_id);
//        MyFragment fragment = new MyFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }


    LayoutInflater inflater;
    Activity context;
    Resources mResources;
    SharedPreferences weatherInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.context = getActivity();
        mResources = context.getResources();
        View content_view = inflater.inflate(R.layout.fragment_layout, container, false);
        weatherInfo = context.getSharedPreferences(area_name + "_weather", Context.MODE_PRIVATE);
        initView(content_view);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        handler.sendEmptyMessage(1);
        //注册接收器，接受来自service的更新结果，或是成功或是失败，再执行相应的动作
        myReceiver = new WeatherInfoRefreshDone(handler);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yhb.action.REFRESH_DONE");
        context.registerReceiver(myReceiver, filter);
        long nowTime = System.currentTimeMillis();
        long refreshTime = weatherInfo.getLong("refreshTime",nowTime);
        if ((nowTime-refreshTime)>=3600000) {
            //启动intentservice，请求并保存信息
            Intent intent = new Intent(context, WeatherInfoRefresh.class);
            intent.putExtra("areaId", area_id);
            intent.putExtra("areaName", area_name);
            context.startService(intent);
        }


        return content_view;
    }

    //实时情况，按id值排列
    TextView refresh_time;
    ImageView live_weather_icon;

    TextView live_weather_weather;
    TextView live_weather_wind_power;
    TextView live_weather_wind_direction;
    TextView live_weather_temperature;
    TextView live_weather_humidity;
    LinearLayout live_weather_air_quality;//需要设置onclick事件的
    TextView live_weather_air_quality_desc;
    //three_hour_weather中的UI
    LinearLayout three_hour_weather_content;
    //six_day中6组各6控件的UI

    LinearLayout check_future;
    LinearLayout six_day_holder;
    //生活建议
    LinearLayout life_suggestion;
    SwipeRefreshLayout mSwipeLayout;
    SimpleDateFormat myFmt;
    private void initView(View content_view) {

        refresh_time=(TextView)content_view.findViewById(R.id.refreshTime);
        myFmt=new SimpleDateFormat("HH:mm 刷新");
        live_weather_icon = (ImageView) content_view.findViewById(R.id.live_weather_icon);
        live_weather_weather = (TextView) content_view.findViewById(R.id.live_weather_weather);
        live_weather_wind_power = (TextView) content_view.findViewById(R.id.live_weather_wind_power);
        live_weather_wind_direction = (TextView) content_view.findViewById(R.id.live_weather_wind_direction);
        live_weather_temperature = (TextView) content_view.findViewById(R.id.live_weather_temperature);
        live_weather_humidity = (TextView) content_view.findViewById(R.id.live_weather_humidity);

        live_weather_air_quality_desc = (TextView) content_view.findViewById(R.id.live_weather_air_quality_desc);
        three_hour_weather_content = (LinearLayout) content_view.findViewById(R.id.three_hour_weather_content);

        six_day_holder = (LinearLayout) content_view.findViewById(R.id.six_day_holder);
        check_future = (LinearLayout) content_view.findViewById(R.id.check_future);
        check_future.setOnClickListener(new View.OnClickListener() {
            boolean down = true;

            @Override
            public void onClick(View v) {
                if (six_day_holder.getChildCount() > 0) {
                    if (down == true) {
                        down = !down;
                        for (int i = 0; i < 3; i++)
                            six_day_holder.getChildAt(5 - i).setVisibility(View.VISIBLE);
                        ((TextView) ((LinearLayout) v).getChildAt(0)).setText("收起");
                        ((TextView) ((LinearLayout) v).getChildAt(1)).setText("︿");

                    } else {
                        down = !down;
                        for (int i = 0; i < 3; i++)
                            six_day_holder.getChildAt(5 - i).setVisibility(View.GONE);
                        ((TextView) ((LinearLayout) v).getChildAt(0)).setText("查看未来几天");
                        ((TextView) ((LinearLayout) v).getChildAt(1)).setText("﹀");
                    }
                }

            }
        });

        life_suggestion = (LinearLayout) content_view.findViewById(R.id.life_suggestion);
        mSwipeLayout = (SwipeRefreshLayout)content_view.findViewById(R.id.myswipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callRefreshService();
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }
    private void callRefreshService(){
        Intent intent = new Intent(context, WeatherInfoRefresh.class);
        intent.putExtra("areaId", area_id);
        intent.putExtra("areaName", area_name);
        context.startService(intent);
    }



//    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(Void... params) {
//            // Simulates a background job.
//            try {
//                Intent intent = new Intent(context, WeatherInfoRefresh.class);
//                intent.putExtra("areaId", area_id);
//                intent.putExtra("areaName", area_name);
//                context.startService(intent);
//            } catch (Exception e) {
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] result) {
//            // Do some stuff here
//
//            // Call onRefreshComplete when the list has been refreshed.
//
//            super.onPostExecute(result);
//        }
//    }
    class ReBack extends AsyncTask{
    @Override
    protected Object doInBackground(Object[] params) {
        LocalInfoBean wb = new LocalInfoBean();

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}

    int doRefresh_count=0;
    private void doRefresh() {
        Date rt = new Date(weatherInfo.getLong("refreshTime", System.currentTimeMillis()));
        refresh_time.setText(myFmt.format(rt));

        String s_six_day_weather = weatherInfo.getString("six_day_weather", "");
        if (s_six_day_weather.length() != 0) {
            six_day_refresh(s_six_day_weather);
        }else if (doRefresh_count<3){
            callRefreshService();
            doRefresh_count+=1;
            return;
        }
        String date = weatherInfo.getString("date", "-1");
        //更新当前天气
        String s_now = weatherInfo.getString("now", "");
        if (s_now.length() != 0) {
            now_refresh(s_now);
        }else if (doRefresh_count<3){
            callRefreshService();
            doRefresh_count+=1;
            return;
        }
//3hours部分
        String s_three_hours_forcast = weatherInfo.getString("3hourForcast", "");

        if (s_three_hours_forcast.length() != 0) {
            three_hour_refresh(s_three_hours_forcast);
        }else if (doRefresh_count<3){
            callRefreshService();
            doRefresh_count+=1;
            return;
        }
        String s_life_suggestion = weatherInfo.getString("life_suggestion", "");
        if (s_life_suggestion.length() != 0) {
            suggestion_refresh(s_life_suggestion);
        }else if (doRefresh_count<3){
            callRefreshService();
            doRefresh_count+=1;
            return;
        }
        doRefresh_count=0;
    }

    private void now_refresh(String s_now) {


        JSONObject now = JSONObject.parseObject(s_now);
        String weather_pic_url = now.getString("weather_pic");
        String s_pic_num = weather_pic_url.substring(weather_pic_url.length() - 6, weather_pic_url.length() - 4);
        int pic_num = Integer.parseInt(s_pic_num);
//            int night_tax=0;
        StringBuffer time_status = new StringBuffer();
        if (weather_pic_url.contains("night")) {
//                night_tax=50;
            time_status.append("n");
        } else {
//                night_tax=0;
            time_status.append("d");
        }
//            imageLoader.displayImage(weather_pic_url, live_weather_icon);
//            live_weather_icon.setImageResource(R.drawable.d00+pic_num+night_tax);
        live_weather_icon.setImageResource(mResources.getIdentifier(time_status.append(s_pic_num).toString(), "drawable", context.getPackageName()));
        live_weather_weather.setText(now.getString("weather"));
        live_weather_wind_power.setText(now.getString("wind_power"));
        live_weather_wind_direction.setText(now.getString("wind_direction"));
        live_weather_temperature.setText(now.getString("temperature") + "℃");
        live_weather_humidity.setText("湿度：" + now.getString("sd"));
        live_weather_air_quality_desc.setText("空气" + now.getJSONObject("aqiDetail").getString("quality"));
            /*refresh_time.setText("气象更新时间" + now.getString("temperature_time"));*/

    }

    private void three_hour_refresh(String s_three_hours_forcast) {


        three_hour_weather_content.removeAllViews();
        JSONArray three_hours_forcast = JSONArray.parseArray(s_three_hours_forcast);
//测试
        for (int i = 0; i < 8; i++) {
//                JSONObject hours_forcast = three_hours_forcast.getJSONObject(i);
//                String[] temp=hours_forcast.getString("wind_power").split(",");
//                time_wind_power[i].setText(temp[0]);
//                time_value[i].setText(hours_forcast.getString("hour"));
//                time_temperature[i].setText(hours_forcast.getString("temperature"));
//                String hours_pic_url = hours_forcast.getString("weather_pic");
////                String s_pic_num=hours_pic_url.substring(hours_pic_url.length() - 6, hours_pic_url.length() - 4);
////                int pic_num=Integer.parseInt(s_pic_num);
////                time_icon[i].setImageResource(R.drawable.i00+pic_num);
//                imageLoader.displayImage(hours_pic_url, time_icon[i]);
            View three_hour_weather_item = inflater.inflate(R.layout.three_hour_weather_item, null);
            TextView time_value = (TextView) three_hour_weather_item.findViewById(R.id.time_value);
            ImageView time_icon = (ImageView) three_hour_weather_item.findViewById(R.id.time_icon);
            TextView time_temperature = (TextView) three_hour_weather_item.findViewById(R.id.time_temperature);
            TextView time_wind_power = (TextView) three_hour_weather_item.findViewById(R.id.time_wind_power);
            try {
                JSONObject hours_forcast = three_hours_forcast.getJSONObject(i);
                String[] temp = hours_forcast.getString("wind_power").split(",");
                time_wind_power.setText(temp[0]);
                time_value.setText(hours_forcast.getString("hour"));
                time_temperature.setText(hours_forcast.getString("temperature") + "℃");
                String hours_pic_url = hours_forcast.getString("weather_pic");
                int night_tax = 0;
                StringBuffer time_status = new StringBuffer();
                if (hours_pic_url.contains("night")) {
                    night_tax = 50;
                    time_status.append("n");
                } else {
                    night_tax = 0;
                    time_status.append("d");
                }
                String s_pic_num = hours_pic_url.substring(hours_pic_url.length() - 6, hours_pic_url.length() - 4);
                int pic_num = Integer.parseInt(s_pic_num);
//                    time_icon.setImageResource(R.drawable.d00+pic_num+night_tax);
                time_icon.setImageResource(mResources.getIdentifier(time_status.append(s_pic_num).toString(), "drawable", context.getPackageName()));
//                    imageLoader.displayImage(hours_pic_url, time_icon);
                three_hour_weather_content.addView(three_hour_weather_item);
            } catch (Exception e) {

            }

        }


    }

    private void six_day_refresh(String s_six_day_weather) {

        six_day_holder.removeAllViews();
        JSONObject o_six_day_weather = JSONObject.parseObject(s_six_day_weather);
        for (int i = 0; i < 6; i++) {
            View six_day_coming_weather_item = inflater.inflate(R.layout.six_day_coming_weather_item, null);
            TextView six_day_weekday = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weekday);
            TextView six_day_day_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_day_temperature);
            TextView six_day_night_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_night_temperature);
            TextView six_day_weather = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weather);
            TextView six_day_wind_power = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_wind_power);

            JSONObject six_weather = o_six_day_weather.getJSONObject("f" + String.valueOf(i + 2) + "_weather");
            if (i == 0) {
                six_day_weekday.setText("明天");
            } else {
                six_day_weekday.setText(Weather_contants.weekday_cn[six_weather.getIntValue("weekday") - 1]);
            }
            six_day_day_temperature.setText(six_weather.getString("day_air_temperature") + "℃");
            six_day_night_temperature.setText(six_weather.getString("night_air_temperature") + "℃");
            six_day_weather.setText(six_weather.getString("day_weather"));
            six_day_wind_power.setText(six_weather.getString("wind_power"));
            six_day_holder.addView(six_day_coming_weather_item);
        }
        for (int i = 0; i < 3; i++)
            six_day_holder.getChildAt(5 - i).setVisibility(View.GONE);


    }

    private void suggestion_refresh(String s_life_suggestion) {


        life_suggestion.removeAllViews();
        JSONObject life_suggetions = JSONObject.parseObject(s_life_suggestion);
//            String[] suggestions = new String[9];
//            suggestions[0]=life_suggetions.getJSONObject("beauty").getString("desc");
//            suggestions[1]=life_suggetions.getJSONObject("clothes").getString("desc");
//            suggestions[2]=life_suggetions.getJSONObject("cl").getString("desc");
//            suggestions[3]=life_suggetions.getJSONObject("cold").getString("desc");
//            suggestions[4]=life_suggetions.getJSONObject("dy").getString("desc");
//            suggestions[5]=life_suggetions.getJSONObject("ac").getString("desc");
//            suggestions[6]=life_suggetions.getJSONObject("uv").getString("desc");
//            suggestions[7]=life_suggetions.getJSONObject("wash_car").getString("desc");
//            suggestions[8]=life_suggetions.getJSONObject("travel").getString("desc");

        for (int i = 0; i < 9; i++) {
            View life_suggestion_item = inflater.inflate(R.layout.life_suggestion_item, null);
            ImageView life_suggestion_icon = (ImageView) life_suggestion_item.findViewById(R.id.life_suggestion_icon);
            TextView life_suggestion_title = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_title);
            TextView life_suggestion_description = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_description);
            try {
                life_suggestion_icon.setImageResource(Weather_contants.life_suggestion_icon_id[i]);
                life_suggestion_title.setText(Weather_contants.life_suggestion_title_string[i]);
                life_suggestion_description.setText(life_suggetions.getJSONObject(Weather_contants.life_suggestion_description_key[i]).getString("desc"));
                life_suggestion.addView(life_suggestion_item);
            } catch (Exception e) {

            }

        }


    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
