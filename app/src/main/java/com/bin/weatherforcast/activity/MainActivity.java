package com.bin.weatherforcast.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bin.weatherforcast.R;
import com.bin.weatherforcast.activity.BaseActivity;
import com.bin.weatherforcast.adapter.MyFragmentAdapter;
import com.bin.weatherforcast.fragment.MyFragment;
import com.bin.weatherforcast.listener.myViewPagerChangedListener;
import com.bin.weatherforcast.receiver.WeatherInfoRefreshDone;
import com.bin.weatherforcast.service.WeatherInfoRefresh;
import com.bin.weatherforcast.utils.Weather_contants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshViewPager();
        viewPager.setCurrentItem(current_page_num);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    ViewPager viewPager;
    SharedPreferences sp;
    BroadcastReceiver myReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initView();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(MainActivity.this));
//        doRefresh();
//        //注册接收器，接受来自service的更新结果，或是成功或是失败，再执行相应的动作
//        myReceiver = new WeatherInfoRefreshDone(handler);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.yhb.action.REFRESH_DONE");
//        registerReceiver(myReceiver, filter);
//
//        //启动intentservice，请求并保存信息
//        Intent intent = new Intent(this, WeatherInfoRefresh.class);
//        intent.putExtra("areaId", "101280101");
//        intent.putExtra("areaName", areaName);
//
//
//        startService(intent);
        initView();
        myReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.yhb.action.AREASEARCH_DONE")){
                    abortBroadcast();
                    int current_page_num=intent.getIntExtra("current_num",0);
                    Log.e("",""+current_page_num);
                    MainActivity.this.current_page_num=current_page_num;
                }


            }
        };
        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction("com.yhb.action.AREASEARCH_DONE");
        intentFilter.setPriority(1000);
        this.registerReceiver(myReceiver, intentFilter);
        initViewPager();
    }
    ImageView locate_icon;
    TextView area_name_text_view;

    private void initView() {
        locate_icon = (ImageView)findViewById(R.id.locate_icon);
        locate_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Position_manager.class);
                startActivityForResult(intent, 0, null);
            }
        });
        area_name_text_view = (TextView)findViewById(R.id.top_bar_area_name);


    }

    int current_page_num=0;
    ArrayList<String> area_name_list=new ArrayList<>();
    ArrayList<String> area_id_list=new ArrayList<>();
    private  void getListData(){
        sp = getSharedPreferences("position_list_always", MODE_PRIVATE);
        String s_list = sp.getString("list", "");
        JSONArray position_JSONArray;
        area_name_list.clear();
        area_id_list.clear();
        if (s_list.length() != 0) {
            position_JSONArray = JSONArray.parseArray(s_list);
            for (int i = 0; i < position_JSONArray.size(); i++) {
                JSONObject jo = position_JSONArray.getJSONObject(i);
                area_name_list.add(jo.getString("area_name"));
                area_id_list.add(jo.getString("area_id"));
            }
        }
    }
    MyFragmentAdapter myFragmentAdapter;
    private  void initViewPager(){
        getListData();
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), area_name_list,area_id_list);
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setAdapter(myFragmentAdapter);
        viewPager.addOnPageChangeListener(new myViewPagerChangedListener(area_name_text_view, area_name_list));
        viewPager.setCurrentItem(current_page_num);
    }
    public void refreshViewPager(){
        getListData();
        myFragmentAdapter.setNewData(area_name_list,area_id_list);
        myFragmentAdapter.notifyDataSetChanged();
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new myViewPagerChangedListener(area_name_text_view, area_name_list));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0&&resultCode==0){
            if(data!=null)
                current_page_num=data.getIntExtra("position",0);

        }

    }

//    //    所在地名
//    ImageView locate_icon;//需要设置onclick事件的
//    TextView area_name;
//    TextView refresh_time;
//    //实时情况，按id值排列
//    ImageView live_weather_icon;
//
//    TextView live_weather_weather;
//    TextView live_weather_wind_power;
//    TextView live_weather_wind_direction;
//    TextView live_weather_temperature;
//    TextView live_weather_humidity;
//    LinearLayout live_weather_air_quality;//需要设置onclick事件的
//    TextView live_weather_air_quality_desc;
//    //three_hour_weather中的UI
//    LinearLayout three_hour_weather_content;
//    //six_day中6组各6控件的UI
//
//    LinearLayout check_future;
//    LinearLayout six_day_holder;
//    //生活建议
//    LinearLayout life_suggestion;
//
//    private void initView() {
//        locate_icon = (ImageView) findViewById(R.id.locate_icon);
//        locate_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Position_manager.class);
//                startActivityForResult(intent, 0, null);
//            }
//        });
//        area_name = (TextView) findViewById(R.id.top_bar_area_name);
//        refresh_time = (TextView) findViewById(R.id.refresh_time);
//
//
//        live_weather_icon = (ImageView) findViewById(R.id.live_weather_icon);
//        live_weather_weather=(TextView)findViewById(R.id.live_weather_weather);
//        live_weather_wind_power=(TextView)findViewById(R.id.live_weather_wind_power);
//        live_weather_wind_direction=(TextView)findViewById(R.id.live_weather_wind_direction);
//        live_weather_temperature=(TextView)findViewById(R.id.live_weather_temperature);
//        live_weather_humidity=(TextView)findViewById(R.id.live_weather_humidity);
//
//        live_weather_air_quality_desc = (TextView) findViewById(R.id.live_weather_air_quality_desc);
//        three_hour_weather_content = (LinearLayout) findViewById(R.id.three_hour_weather_content);
//
//        six_day_holder = (LinearLayout) findViewById(R.id.six_day_holder);
//        check_future = (LinearLayout) findViewById(R.id.check_future);
//        check_future.setOnClickListener(new View.OnClickListener() {
//            boolean down = true;
//
//            @Override
//            public void onClick(View v) {
//
//                if (down == true) {
//                    down = !down;
//                    for (int i = 0; i < 3; i++)
//                        six_day_holder.getChildAt(5 - i).setVisibility(View.VISIBLE);
//                    ((TextView) ((LinearLayout) v).getChildAt(0)).setText("收起");
//                    ((TextView) ((LinearLayout) v).getChildAt(1)).setText("︿");
//
//                } else {
//                    down = !down;
//                    for (int i = 0; i < 3; i++)
//                        six_day_holder.getChildAt(5 - i).setVisibility(View.GONE);
//                    ((TextView) ((LinearLayout) v).getChildAt(0)).setText("查看未来几天");
//                    ((TextView) ((LinearLayout) v).getChildAt(1)).setText("﹀");
//                }
//            }
//        });
//
//        life_suggestion = (LinearLayout) findViewById(R.id.life_suggestion);
//
//    }
//
//    private String areaName = "广州";
//
//    public void setArea_name(String area_name) {
//        areaName = area_name;
//    }
//
//    private void doRefresh() {
//        area_name.setText(areaName);
//        SharedPreferences weatherInfo = getSharedPreferences(areaName + "_weather", MODE_PRIVATE);
//
//        six_day_refresh(weatherInfo);
//        now_refresh(weatherInfo);
//        three_hour_refresh(weatherInfo);
//        suggestion_refresh(weatherInfo);
//    }
//
//    private void now_refresh(SharedPreferences weatherInfo) {
//        String date = weatherInfo.getString("date", "-1");
//        //更新当前天气
//        String s_now = weatherInfo.getString("now", "");
//        if (s_now.length() != 0) {
//            JSONObject now = JSONObject.parseObject(s_now);
//            String weather_pic_url = now.getString("weather_pic");
//            String s_pic_num=weather_pic_url.substring(weather_pic_url.length() - 6, weather_pic_url.length() - 4);
//            int pic_num=Integer.parseInt(s_pic_num);
//            int night_tax=0;
//            if (weather_pic_url.contains("night")){
//                night_tax=50;
//            }else{
//                night_tax=0;
//            }
////            imageLoader.displayImage(weather_pic_url, live_weather_icon);
//            live_weather_icon.setImageResource(R.drawable.d00+pic_num+night_tax);
//            live_weather_weather.setText(now.getString("weather"));
//            live_weather_wind_power.setText(now.getString("wind_power"));
//            live_weather_wind_direction.setText(now.getString("wind_direction"));
//            live_weather_temperature.setText(now.getString("temperature") + "℃");
//            live_weather_humidity.setText("湿度：" + now.getString("sd"));
//            live_weather_air_quality_desc.setText("空气" + now.getJSONObject("aqiDetail").getString("quality"));
//            refresh_time.setText("气象更新时间" + now.getString("temperature_time"));
//        }
//    }
//
//    private void three_hour_refresh(SharedPreferences weatherInfo) {
//        //3hours部分
//        String s_three_hours_forcast = weatherInfo.getString("3hourForcast", "");
//
//        if (s_three_hours_forcast.length() != 0) {
//            three_hour_weather_content.removeAllViews();
//            JSONArray three_hours_forcast = JSONArray.parseArray(s_three_hours_forcast);
//
//            for (int i = 0; i < 8; i++) {
////                JSONObject hours_forcast = three_hours_forcast.getJSONObject(i);
////                String[] temp=hours_forcast.getString("wind_power").split(",");
////                time_wind_power[i].setText(temp[0]);
////                time_value[i].setText(hours_forcast.getString("hour"));
////                time_temperature[i].setText(hours_forcast.getString("temperature"));
////                String hours_pic_url = hours_forcast.getString("weather_pic");
//////                String s_pic_num=hours_pic_url.substring(hours_pic_url.length() - 6, hours_pic_url.length() - 4);
//////                int pic_num=Integer.parseInt(s_pic_num);
//////                time_icon[i].setImageResource(R.drawable.i00+pic_num);
////                imageLoader.displayImage(hours_pic_url, time_icon[i]);
//                View three_hour_weather_item = getLayoutInflater().inflate(R.layout.three_hour_weather_item, null);
//                TextView time_value = (TextView) three_hour_weather_item.findViewById(R.id.time_value);
//                ImageView time_icon = (ImageView) three_hour_weather_item.findViewById(R.id.time_icon);
//                TextView time_temperature = (TextView) three_hour_weather_item.findViewById(R.id.time_temperature);
//                TextView time_wind_power = (TextView) three_hour_weather_item.findViewById(R.id.time_wind_power);
//                try {
//                    JSONObject hours_forcast = three_hours_forcast.getJSONObject(i);
//                    String[] temp = hours_forcast.getString("wind_power").split(",");
//                    time_wind_power.setText(temp[0]);
//                    time_value.setText(hours_forcast.getString("hour"));
//                    time_temperature.setText(hours_forcast.getString("temperature") + "℃");
//                    String hours_pic_url = hours_forcast.getString("weather_pic");
//                    int night_tax=0;
//                    if (hours_pic_url.contains("night")) {
//                        night_tax = 50;
//                    }else{
//                        night_tax=0;
//                    }
//                    String s_pic_num=hours_pic_url.substring(hours_pic_url.length() - 6, hours_pic_url.length() - 4);
//                    int pic_num=Integer.parseInt(s_pic_num);
//                    time_icon.setImageResource(R.drawable.d00+pic_num+night_tax);
////                    imageLoader.displayImage(hours_pic_url, time_icon);
//                    three_hour_weather_content.addView(three_hour_weather_item);
//                } catch (Exception e) {
//
//                }
//
//            }
//
//        }
//
//    }
//
//    private void six_day_refresh(SharedPreferences weatherInfo) {
//        String s_six_day_weather = weatherInfo.getString("six_day_weather", "");
//        if (s_six_day_weather.length() != 0) {
//            six_day_holder.removeAllViews();
//            JSONObject o_six_day_weather = JSONObject.parseObject(s_six_day_weather);
//            for (int i = 0; i < 6; i++) {
//                View six_day_coming_weather_item = getLayoutInflater().inflate(R.layout.six_day_coming_weather_item, null);
//                TextView six_day_weekday = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weekday);
//                TextView six_day_day_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_day_temperature);
//                TextView six_day_night_temperature = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_night_temperature);
//                TextView six_day_weather = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_weather);
//                TextView six_day_wind_power = (TextView) six_day_coming_weather_item.findViewById(R.id.six_day_wind_power);
//
//                JSONObject six_weather = o_six_day_weather.getJSONObject("f" + String.valueOf(i + 2) + "_weather");
//                if (i == 0) {
//                    six_day_weekday.setText("明天");
//                } else {
//                    six_day_weekday.setText(Weather_contants.weekday_cn[six_weather.getIntValue("weekday") - 1]);
//                }
//                six_day_day_temperature.setText(six_weather.getString("day_air_temperature") + "℃");
//                six_day_night_temperature.setText(six_weather.getString("night_air_temperature") + "℃");
//                six_day_weather.setText(six_weather.getString("day_weather"));
//                six_day_wind_power.setText(six_weather.getString("wind_power"));
//                six_day_holder.addView(six_day_coming_weather_item);
//            }
//            for (int i = 0; i < 3; i++)
//                six_day_holder.getChildAt(5 - i).setVisibility(View.GONE);
//
//        }
//    }
//
//    private void suggestion_refresh(SharedPreferences weatherInfo) {
//        String s_life_suggestion = weatherInfo.getString("life_suggestion", "");
//        if (s_life_suggestion.length() != 0) {
//            life_suggestion.removeAllViews();
//            JSONObject life_suggetions = JSONObject.parseObject(s_life_suggestion);
////            String[] suggestions = new String[9];
////            suggestions[0]=life_suggetions.getJSONObject("beauty").getString("desc");
////            suggestions[1]=life_suggetions.getJSONObject("clothes").getString("desc");
////            suggestions[2]=life_suggetions.getJSONObject("cl").getString("desc");
////            suggestions[3]=life_suggetions.getJSONObject("cold").getString("desc");
////            suggestions[4]=life_suggetions.getJSONObject("dy").getString("desc");
////            suggestions[5]=life_suggetions.getJSONObject("ac").getString("desc");
////            suggestions[6]=life_suggetions.getJSONObject("uv").getString("desc");
////            suggestions[7]=life_suggetions.getJSONObject("wash_car").getString("desc");
////            suggestions[8]=life_suggetions.getJSONObject("travel").getString("desc");
//
//            for (int i = 0; i < 9; i++) {
//                View life_suggestion_item = getLayoutInflater().inflate(R.layout.life_suggestion_item, null);
//                ImageView life_suggestion_icon = (ImageView) life_suggestion_item.findViewById(R.id.life_suggestion_icon);
//                TextView life_suggestion_title = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_title);
//                TextView life_suggestion_description = (TextView) life_suggestion_item.findViewById(R.id.life_suggestion_description);
//                try {
//                    life_suggestion_icon.setImageResource(Weather_contants.life_suggestion_icon_id[i]);
//                    life_suggestion_title.setText(Weather_contants.life_suggestion_title_string[i]);
//                    life_suggestion_description.setText(life_suggetions.getJSONObject(Weather_contants.life_suggestion_description_key[i]).getString("desc"));
//                    life_suggestion.addView(life_suggestion_item);
//                } catch (Exception e) {
//
//                }
//
//            }
//
//        }
//    }
}
