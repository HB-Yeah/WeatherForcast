package com.bin.weatherforcast.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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

        areaId=intent.getStringExtra("areaId");
        areaName=intent.getStringExtra("areaName");
        doRefreshRequest();
    }


    private void setAsyncHttpResponseHandler() {
        Log.e("","设置请求");
        resHandler = new AsyncHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                //做一些异常处理
                Intent intent = new Intent();
                intent.setAction("com.yhb.action.REFRESH_DONE_"+areaId);
                intent.putExtra("isDone", false);
                sendBroadcast(intent);

                e.printStackTrace();
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {


                    SharedPreferences weatherInfo = getSharedPreferences(areaName+"_weather", MODE_PRIVATE);//地名区分各地区的文件
                    SharedPreferences.Editor editor = weatherInfo.edit();


                    JSONObject ja = JSONObject.parseObject(new String(responseBody, "utf-8"));
                    JSONObject ja_body = ja.getJSONObject("showapi_res_body");
                    //提取f1~f7的内容
                    JSONObject[] f = new JSONObject[7];
                    for (int i =0;i<7;i++){
                        f[i]=ja_body.getJSONObject("f"+String.valueOf(i+1));
                    }
                    //获取当天日期,用于判断是否更新后六天数据，否则只更新当前数据
                    String date = f[0].getString("day");
                    editor.putString("date",date);

                    //获取实时天气
                    JSONObject now = ja_body.getJSONObject("now");
                    editor.putString("now", now.toString());
                    String weather_pic_url = now.getString("weather_pic");


                    //在f1中提取3hourForcast的内容
                    JSONArray three_hours_forcast = f[0].getJSONArray("3hourForcast");
                    editor.putString("3hourForcast",three_hours_forcast.toString());

                    //如果日期没变，以下内容不需更新
                    String old_date = weatherInfo.getString("date", "-1");
                    if(old_date.equals(date)){
                        //在f1中提取生活建议内容
                        JSONObject life_suggestion = f[0].getJSONObject("index");
                        editor.putString("life_suggestion",life_suggestion.toString());

                        //挑出未来六天中所需的信息
                        JSONObject six_day_weather = new JSONObject();
                        for(int i=0;i<6;i++){
                            JSONObject f_2_7_weather = new JSONObject();
                            f_2_7_weather.put("weekday", f[i + 1].getString("weekday"));
                            f_2_7_weather.put("day_weather", f[i + 1].getString("day_weather"));
                            f_2_7_weather.put("day_air_temperature", f[i + 1].getString("day_air_temperature"));
                            f_2_7_weather.put("night_air_temperature", f[i + 1].getString("night_air_temperature"));
                            f_2_7_weather.put("wind_power", f[i + 1].getString("day_wind_power"));

                            six_day_weather.put("f" + String.valueOf(i + 2) + "_weather", f_2_7_weather);
                        }
                        editor.putString("six_day_weather",six_day_weather.toString());

                    }

                    long refreshTime = System.currentTimeMillis();
                    editor.putLong("refreshTime",refreshTime);
                    editor.commit();
                    //处理完毕，通知Receiver
                    Intent intent = new Intent();
                    intent.setAction("com.yhb.action.REFRESH_DONE_"+areaId);
                    intent.putExtra("isDone", true);
                    sendBroadcast(intent);
                    Log.e("","请求完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
    private void doRefreshRequest() {
        Log.e("","开始请求"+areaName);

        new ShowApiRequest( "http://route.showapi.com/9-2", "17548", "cf5dc4b86ae74a9c8cf1717611406754")
                .setResponseHandler(resHandler)
                .addTextPara("areaid", areaId)
                .addTextPara("needMoreDay", "1")
                .addTextPara("needIndex", "1")
                .addTextPara("needHourData", "1")
                .addTextPara("need3HourForcast", "1")
                .addTextPara("needAlarm", "0")
                .post();
    }
}
