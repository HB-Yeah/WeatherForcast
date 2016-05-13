package com.bin.weatherforcast.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bin.weatherforcast.utils.NetInfoBean;
import com.bin.weatherforcast.utils.Weather_contants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stranger on 2016/4/8.
 */
public class WeatherInfoRefresh extends IntentService {
    AsyncHttpResponseHandler resHandler;
    String areaId;
    String areaName;

    public WeatherInfoRefresh() {

        super("WeatherInfoRefresh");
        setAsyncHttpResponseHandler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        areaId = intent.getStringExtra("areaId");
        areaName = intent.getStringExtra("areaName");
        doRefreshRequest();
    }


    private void setAsyncHttpResponseHandler() {
        Log.e("", "设置请求");
        resHandler = new AsyncHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                //做一些异常处理
                Intent intent = new Intent();
                intent.setAction("com.yhb.action.REFRESH_DONE_" + areaId);
                intent.putExtra("isDone", false);
                sendBroadcast(intent);

                e.printStackTrace();
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    NetInfoBean nib = new NetInfoBean();
                    SharedPreferences weatherInfo = getSharedPreferences(areaName + "_weather", MODE_PRIVATE);//地名区分各地区的文件
                    SharedPreferences.Editor editor = weatherInfo.edit();


                    JSONObject ja = JSONObject.parseObject(new String(responseBody, "utf-8"));
                    JSONObject ja_body = ja.getJSONObject("showapi_res_body");
                    //提取f1~f7的内容
                    JSONObject[] f = new JSONObject[7];
                    for (int i = 0; i < 7; i++) {
                        f[i] = ja_body.getJSONObject("f" + String.valueOf(i + 1));
                    }
                    //获取当天日期,用于判断是否更新后六天数据，否则只更新当前数据
                    String date = f[0].getString("day");
                    editor.putString("date", date);

                    //获取实时天气
                    JSONObject now = ja_body.getJSONObject("now");
                    editor.putString("now", now.toString());
                    setNow(now, nib);


                    //在f1中提取3hourForcast的内容
                    JSONArray three_hours_forcast = f[0].getJSONArray("3hourForcast");
                    editor.putString("3hourForcast", three_hours_forcast.toString());
                    setThreeHours(three_hours_forcast, nib);

                    //在f1中提取生活建议内容
                    JSONObject life_suggestion = f[0].getJSONObject("index");
                    editor.putString("life_suggestion", life_suggestion.toString());
                    suggestion_refresh(life_suggestion, nib);

                    //挑出未来六天中所需的信息
                    JSONObject six_day_weather = new JSONObject();
                    for (int i = 0; i < 6; i++) {
                        JSONObject f_2_7_weather = new JSONObject();
                        f_2_7_weather.put("weekday", f[i + 1].getString("weekday"));
                        f_2_7_weather.put("day_weather", f[i + 1].getString("day_weather"));
                        f_2_7_weather.put("day_air_temperature", f[i + 1].getString("day_air_temperature"));
                        f_2_7_weather.put("night_air_temperature", f[i + 1].getString("night_air_temperature"));
                        f_2_7_weather.put("wind_power", f[i + 1].getString("day_wind_power"));

                        six_day_weather.put("f" + String.valueOf(i + 2) + "_weather", f_2_7_weather);
                    }
                    editor.putString("six_day_weather", six_day_weather.toString());
                    setSixDay(six_day_weather, nib);


                    long refreshTime = System.currentTimeMillis();
                    editor.putLong("refreshTime", refreshTime);
                    editor.commit();
                    //处理完毕，通知Receiver
                    Intent intent = new Intent();
                    intent.setAction("com.yhb.action.REFRESH_DONE_" + areaId);
                    intent.putExtra("isDone", true);
                    intent.putExtra(areaId, nib);
                    sendBroadcast(intent);
                    Log.e("", "请求完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private void doRefreshRequest() {
        Log.e("", "开始请求" + areaName);

        new ShowApiRequest("http://route.showapi.com/9-2", "17548", "cf5dc4b86ae74a9c8cf1717611406754")
                .setResponseHandler(resHandler)
                .addTextPara("areaid", areaId)
                .addTextPara("needMoreDay", "1")
                .addTextPara("needIndex", "1")
                .addTextPara("needHourData", "1")
                .addTextPara("need3HourForcast", "1")
                .addTextPara("needAlarm", "0")
                .post();
    }

    private void suggestion_refresh(JSONObject life_suggetions, NetInfoBean nib) {
        String[] suggesionDesc = new String[9];
        for (int i = 0; i < 9; i++) {
            try {
                suggesionDesc[i] = life_suggetions.getJSONObject(Weather_contants.life_suggestion_description_key[i]).getString("desc");
            } catch (Exception e) {
                suggesionDesc[i] = null;
            }
        }
        nib.setSuggestionDesc(suggesionDesc);

    }

    private void setSixDay(JSONObject o_six_day_weather, NetInfoBean nib) {
        String[] sixDayDayTemperature = new String[6];
        String[] sixDayNightTemperature = new String[6];
        String[] sixDayWeather = new String[6];
        String[] sixDayWindPower = new String[6];
        String[] sixDayWeekday = new String[6];
        for (int i = 0; i < 6; i++) {
            JSONObject six_weather = o_six_day_weather.getJSONObject("f" + String.valueOf(i + 2) + "_weather");
            if (i == 0) {
                sixDayWeekday[i] = "明天";
            } else {
                sixDayWeekday[i] = Weather_contants.weekday_cn[six_weather.getIntValue("weekday") - 1];
            }
            sixDayDayTemperature[i] = six_weather.getString("day_air_temperature") + "℃";
            sixDayNightTemperature[i] = six_weather.getString("night_air_temperature") + "℃";
            sixDayWeather[i] = six_weather.getString("day_weather");
            sixDayWindPower[i] = six_weather.getString("wind_power");
        }
        nib.setSixDayWeekday(sixDayWeekday);
        nib.setSixDayDayTemperature(sixDayDayTemperature);
        nib.setSixDayNightTemperature(sixDayNightTemperature);
        nib.setSixDayWeather(sixDayWeather);
        nib.setSixDayWindPower(sixDayWindPower);
    }

    private void setThreeHours(JSONArray three_hours_forcast, NetInfoBean nib) {
        int length = three_hours_forcast.size();
        String[] threeHoursIcon = new String[length];
        String[] threeHoursWindPower = new String[length];
        String[] threeHoursValue = new String[length];
        String[] threeHoursTemperature = new String[length];

        for (int i = 0; i < length; i++) {
            JSONObject hours_forcast = three_hours_forcast.getJSONObject(i);
            String[] temp = hours_forcast.getString("wind_power").split(",");
            threeHoursWindPower[i] = temp[0];
            threeHoursValue[i] = hours_forcast.getString("hour");
            threeHoursTemperature[i] = hours_forcast.getString("temperature") + "℃";
            String hours_pic_url = hours_forcast.getString("weather_pic");
            StringBuffer time_status = new StringBuffer();
            if (hours_pic_url.contains("night")) {
                time_status.append("n");
            } else {
                time_status.append("d");
            }
            String s_pic_num = hours_pic_url.substring(hours_pic_url.length() - 6, hours_pic_url.length() - 4);
            threeHoursIcon[i] = time_status.append(s_pic_num).toString();
        }
        nib.setThreeHoursIcon(threeHoursIcon);
        nib.setThreeHoursWindPower(threeHoursWindPower);
        nib.setThreeHoursValue(threeHoursValue);
        nib.setThreeHoursTemperature(threeHoursTemperature);
    }

    private void setNow(JSONObject now, NetInfoBean nib) {
        String weather_pic_url = now.getString("weather_pic");
        String s_pic_num = weather_pic_url.substring(weather_pic_url.length() - 6, weather_pic_url.length() - 4);
        StringBuffer time_status = new StringBuffer();
        if (weather_pic_url.contains("night")) {
            time_status.append("n");
        } else {
            time_status.append("d");
        }
        nib.setLiveIcon(time_status.append(s_pic_num).toString());
        nib.setLiveWeather(now.getString("weather"));
        nib.setLiveWindPower(now.getString("wind_power"));
        nib.setLiveWindDirection(now.getString("wind_direction"));
        nib.setLiveTemperature(now.getString("temperature") + "℃");
        nib.setLiveHumidity("湿度：" + now.getString("sd"));
        nib.setLiveAirDesc("空气" + now.getJSONObject("aqiDetail").getString("quality"));
    }
}
