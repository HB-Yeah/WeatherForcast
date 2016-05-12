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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.R;
import com.bin.weatherforcast.listener.LocalInfoSetListener;
import com.bin.weatherforcast.receiver.WeatherInfoRefreshDone;
import com.bin.weatherforcast.service.WeatherInfoRefresh;
import com.bin.weatherforcast.utils.LocalInfoBeanManager;
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

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    WeatherInfoRefreshDone myReceiver;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    doRefresh();
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);
                    }
                    break;
            }

        }
    };


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
        //注册接收器，接受来自service的更新结果，或是成功或是失败，再执行相应的动作
        myReceiver = new WeatherInfoRefreshDone(handler);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yhb.action.REFRESH_DONE");
        context.registerReceiver(myReceiver, filter);
        doRefresh();
        long nowTime = System.currentTimeMillis();
        long refreshTime = weatherInfo.getLong("refreshTime", nowTime);
        if ((nowTime - refreshTime) >= 3600000) {
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

        refresh_time = (TextView) content_view.findViewById(R.id.refreshTime);
        myFmt = new SimpleDateFormat("HH:mm 刷新");
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
        mSwipeLayout = (SwipeRefreshLayout) content_view.findViewById(R.id.myswipeRefreshLayout);
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

    private void callRefreshService() {
        Intent intent = new Intent(context, WeatherInfoRefresh.class);
        intent.putExtra("areaId", area_id);
        intent.putExtra("areaName", area_name);
        context.startService(intent);
    }

    LocalInfoBeanManager libm;
    LocalInfoBean lib;
    private void doRefresh() {
        if(isLocalDataExist()){
            Date rt = new Date(weatherInfo.getLong("refreshTime", System.currentTimeMillis()));
            refresh_time.setText(myFmt.format(rt));

            String s_six_day_weather = weatherInfo.getString("six_day_weather", "");
            String s_now = weatherInfo.getString("now", "");
            String s_three_hours_forcast = weatherInfo.getString("3hourForcast", "");
            String s_life_suggestion = weatherInfo.getString("life_suggestion", "");
            if (!s_six_day_weather.equals("") && !s_now.equals("")&& !s_three_hours_forcast.equals("") && !s_life_suggestion.equals("")) {
                libm = new LocalInfoBeanManager(s_six_day_weather, s_now, s_three_hours_forcast, s_life_suggestion);
                libm.setListener(new LocalInfoSetListener() {
                    @Override
                    public void afterSetDone() {
                        lib = libm.getBean();
                        now_refresh(lib);
                        three_hour_refresh(lib);
                        six_day_refresh(lib);
                        suggestion_refresh(lib);
                    }
                });
            } else {
                callRefreshService();
            }


        }else {
            Toast.makeText(context,"更新失败了~",Toast.LENGTH_SHORT);
        }
    }

    private void now_refresh(LocalInfoBean lib) {
        String iconString=lib.getLiveIcon();
        int id=mResources.getIdentifier(iconString, "drawable", context.getPackageName());
        live_weather_icon.setImageResource(id);
        live_weather_weather.setText(lib.getLiveWeather());
        live_weather_wind_power.setText(lib.getLiveWindPower());
        live_weather_wind_direction.setText(lib.getLiveWindDirection());
        live_weather_temperature.setText(lib.getLiveTemperature());
        live_weather_humidity.setText(lib.getLiveHumidity());
        live_weather_air_quality_desc.setText(lib.getLiveAirDesc());

    }

    private void three_hour_refresh(LocalInfoBean lib) {
        //由于此处的数据量不稳定，所以用length来控制view
        three_hour_weather_content.removeAllViews();
        String[] threeHoursIcon = lib.getThreeHoursIcon();
        String[] threeHoursWindPower = lib.getThreeHoursWindPower();
        String[] threeHoursValue = lib.getThreeHoursValue();
        String[] threeHoursTemperature = lib.getThreeHoursTemperature();
        int length = threeHoursIcon.length;
        for (int i = 0; i < length; i++) {
            View three_hour_weather_item = inflater.inflate(R.layout.three_hour_weather_item, null);
            TextView time_value = (TextView) three_hour_weather_item.findViewById(R.id.time_value);
            ImageView time_icon = (ImageView) three_hour_weather_item.findViewById(R.id.time_icon);
            TextView time_temperature = (TextView) three_hour_weather_item.findViewById(R.id.time_temperature);
            TextView time_wind_power = (TextView) three_hour_weather_item.findViewById(R.id.time_wind_power);
            time_wind_power.setText(threeHoursWindPower[i]);
            time_value.setText(threeHoursValue[i]);
            time_temperature.setText(threeHoursTemperature[i]);
            time_icon.setImageResource(mResources.getIdentifier(threeHoursIcon[i], "drawable", context.getPackageName()));
            three_hour_weather_content.addView(three_hour_weather_item);
        }


    }

    private void six_day_refresh(LocalInfoBean lib) {
        //6个格式相同的段落，直接addview，省下设置id的功夫
        six_day_holder.removeAllViews();
        String[] sixDayDayTemperature = lib.getSixDayDayTemperature();
        String[] sixDayNightTemperature = lib.getSixDayNightTemperature();
        String[] sixDayWeather = lib.getSixDayWeather();
        String[] sixDayWindPower = lib.getSixDayWindPower();
        String[] sixDayWeekday = lib.getSixDayWeekday();
        for (int i = 0; i < 6; i++) {
            View six_day_coming_weather_item = inflater.inflate(R.layout.six_day_coming_weather_item, null);
            TextView six_day_weekday = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weekday);
            TextView six_day_day_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_day_temperature);
            TextView six_day_night_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_night_temperature);
            TextView six_day_weather = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weather);
            TextView six_day_wind_power = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_wind_power);

            six_day_weekday.setText(sixDayWeekday[i]);
            six_day_day_temperature.setText(sixDayDayTemperature[i]);
            six_day_night_temperature.setText(sixDayNightTemperature[i]);
            six_day_weather.setText(sixDayWeather[i]);
            six_day_wind_power.setText(sixDayWindPower[i]);

            six_day_holder.addView(six_day_coming_weather_item);
        }
        for (int i = 0; i < 3; i++)
            six_day_holder.getChildAt(5 - i).setVisibility(View.GONE);


    }

    private void suggestion_refresh(LocalInfoBean lib) {
        //这里的数据也是不稳定
        life_suggestion.removeAllViews();
        String[] suggestionDesc = lib.getSuggestionDesc();
        for (int i = 0; i < 9; i++) {
            if(suggestionDesc[i]!=null){
                View life_suggestion_item = inflater.inflate(R.layout.life_suggestion_item, null);
                ImageView life_suggestion_icon = (ImageView) life_suggestion_item.findViewById(R.id.life_suggestion_icon);
                TextView life_suggestion_title = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_title);
                TextView life_suggestion_description = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_description);

                life_suggestion_icon.setImageResource(Weather_contants.life_suggestion_icon_id[i]);
                life_suggestion_title.setText(Weather_contants.life_suggestion_title_string[i]);
                life_suggestion_description.setText(suggestionDesc[i]);
                life_suggestion.addView(life_suggestion_item);
            }
        }


    }
    private boolean isLocalDataExist(){
        long refreshTime = weatherInfo.getLong("refreshTime", -1);
        if (refreshTime==-1)
            return false;
        else
            return true;
    }
    @Override
    public void onDestroy() {
        context.unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
