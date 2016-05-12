package com.bin.weatherforcast.utils;

import java.io.Serializable;

/**
 * Created by Stranger on 2016/5/11.
 */
public class LocalInfoBean implements Serializable{
    private long refreshTime;

    private String liveIcon;
    private String liveWeather;
    private String liveWindPower;
    private String liveWindDirection;
    private String liveTemperature;
    private String liveHumidity;
    private String liveAirDesc;

    private String[] threeHoursIcon;
    private String[] threeHoursWindPower;
    private String[] threeHoursValue;
    private String[] threeHoursTemperature;

    private String[] sixDayDayTemperature;
    private String[] sixDayNightTemperature;
    private String[] sixDayWeather;
    private String[] sixDayWindPower;
    private String[] sixDayWeekday;

    private String[] suggestionDesc;

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String[] getSuggestionDesc() {
        return suggestionDesc;
    }

    public void setSuggestionDesc(String[] suggestionDesc) {
        this.suggestionDesc = suggestionDesc;
    }


    public String[] getSixDayWeekday() {
        return sixDayWeekday;
    }

    public void setSixDayWeekday(String[] sixDayWeekday) {
        this.sixDayWeekday = sixDayWeekday;
    }

    public String[] getSixDayWindPower() {
        return sixDayWindPower;
    }

    public void setSixDayWindPower(String[] sixDayWindPower) {
        this.sixDayWindPower = sixDayWindPower;
    }

    public String[] getSixDayDayTemperature() {
        return sixDayDayTemperature;
    }

    public void setSixDayDayTemperature(String[] sixDayDayTemperature) {
        this.sixDayDayTemperature = sixDayDayTemperature;
    }

    public String[] getSixDayNightTemperature() {
        return sixDayNightTemperature;
    }

    public void setSixDayNightTemperature(String[] sixDayNightTemperature) {
        this.sixDayNightTemperature = sixDayNightTemperature;
    }

    public String[] getSixDayWeather() {
        return sixDayWeather;
    }

    public void setSixDayWeather(String[] sixDayWeather) {
        this.sixDayWeather = sixDayWeather;
    }


    public String[] getThreeHoursIcon() {
        return threeHoursIcon;
    }

    public void setThreeHoursIcon(String[] threeHoursIcon) {
        this.threeHoursIcon = threeHoursIcon;
    }

    public String[] getThreeHoursWindPower() {
        return threeHoursWindPower;
    }

    public void setThreeHoursWindPower(String[] threeHoursWindPower) {
        this.threeHoursWindPower = threeHoursWindPower;
    }

    public String[] getThreeHoursValue() {
        return threeHoursValue;
    }

    public void setThreeHoursValue(String[] threeHoursValue) {
        this.threeHoursValue = threeHoursValue;
    }

    public String[] getThreeHoursTemperature() {
        return threeHoursTemperature;
    }

    public void setThreeHoursTemperature(String[] threeHoursTemperature) {
        this.threeHoursTemperature = threeHoursTemperature;
    }


    public String getLiveWindDirection() {
        return liveWindDirection;
    }

    public void setLiveWindDirection(String liveWindDirection) {
        this.liveWindDirection = liveWindDirection;
    }

    public String getLiveIcon() {
        return liveIcon;
    }

    public void setLiveIcon(String liveIcon) {
        this.liveIcon = liveIcon;
    }

    public String getLiveWeather() {
        return liveWeather;
    }

    public void setLiveWeather(String liveWeather) {
        this.liveWeather = liveWeather;
    }

    public String getLiveWindPower() {
        return liveWindPower;
    }

    public void setLiveWindPower(String liveWindPower) {
        this.liveWindPower = liveWindPower;
    }

    public String getLiveTemperature() {
        return liveTemperature;
    }

    public void setLiveTemperature(String liveTemperature) {
        this.liveTemperature = liveTemperature;
    }

    public String getLiveHumidity() {
        return liveHumidity;
    }

    public void setLiveHumidity(String liveHumidity) {
        this.liveHumidity = liveHumidity;
    }

    public String getLiveAirDesc() {
        return liveAirDesc;
    }

    public void setLiveAirDesc(String liveAirDesc) {
        this.liveAirDesc = liveAirDesc;
    }


}
