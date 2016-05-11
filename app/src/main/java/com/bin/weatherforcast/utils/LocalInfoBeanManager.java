package com.bin.weatherforcast.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bin.weatherforcast.listener.LocalInfoSetListener;

/**
 * Created by Stranger on 2016/5/9.
 */
public class LocalInfoBeanManager {
    LocalInfoSetListener listener;
    LocalInfoBean bean;
    //需要处理的四个串
    String sixDay;
    String now;
    String threeHours;
    String lifeSuggestion;

    public void setListener(LocalInfoSetListener l) {
        listener = l;
    }

    public LocalInfoBeanManager(String six_day, String s_now, String three_hours, String life_suggestion) {
        bean = new LocalInfoBean();
        sixDay = six_day;
        now = s_now;
        threeHours = three_hours;
        lifeSuggestion = life_suggestion;
        new setInfoTask().execute();
    }

    public LocalInfoBean getBean() {
        return bean;
    }


    class setInfoTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            setNow(now);
            setThreeHours(threeHours);
            setSixDay(sixDay);
            suggestion_refresh(lifeSuggestion);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (listener != null)
                listener.afterSetDone();

        }
    }

    private void suggestion_refresh(String s_life_suggestion) {
        JSONObject life_suggetions = JSONObject.parseObject(s_life_suggestion);
        String[] suggesionDesc=new String[9];
        for (int i = 0; i <9; i++){
                try{
                    suggesionDesc[i]=life_suggetions.getJSONObject(Weather_contants.life_suggestion_description_key[i]).getString("desc");
                }catch (Exception e){
                    suggesionDesc[i]=null;
                }
        }
        bean.setSuggestionDesc(suggesionDesc);

    }

    private void setSixDay(String s_six_day_weather) {
        String[] sixDayDayTemperature = new String[6];
        String[] sixDayNightTemperature = new String[6];
        String[] sixDayWeather = new String[6];
        String[] sixDayWindPower = new String[6];
        String[] sixDayWeekday = new String[6];
        JSONObject o_six_day_weather = JSONObject.parseObject(s_six_day_weather);
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
        bean.setSixDayWeekday(sixDayWeekday);
        bean.setSixDayDayTemperature(sixDayDayTemperature);
        bean.setSixDayNightTemperature(sixDayNightTemperature);
        bean.setSixDayWeather(sixDayWeather);
        bean.setSixDayWindPower(sixDayWindPower);
    }

    private void setThreeHours(String s_three_hours_forcast) {
        JSONArray three_hours_forcast = JSONArray.parseArray(s_three_hours_forcast);
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
        bean.setThreeHoursIcon(threeHoursIcon);
        bean.setThreeHoursWindPower(threeHoursWindPower);
        bean.setThreeHoursValue(threeHoursValue);
        bean.setThreeHoursTemperature(threeHoursTemperature);
    }

    private void setNow(String s_now) {
        JSONObject now = JSONObject.parseObject(s_now);
        String weather_pic_url = now.getString("weather_pic");
        String s_pic_num = weather_pic_url.substring(weather_pic_url.length() - 6, weather_pic_url.length() - 4);
        StringBuffer time_status = new StringBuffer();
        if (weather_pic_url.contains("night")) {
            time_status.append("n");
        } else {
            time_status.append("d");
        }
        bean.setLiveIcon(time_status.append(s_pic_num).toString());
        bean.setLiveWeather(now.getString("weather"));
        bean.setLiveWindPower(now.getString("wind_power"));
        bean.setLiveWindDirection(now.getString("wind_direction"));
        bean.setLiveTemperature(now.getString("temperature") + "℃");
        bean.setLiveHumidity("湿度：" + now.getString("sd"));
        bean.setLiveAirDesc("空气" + now.getJSONObject("aqiDetail").getString("quality"));
    }
}

