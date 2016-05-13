package com.bin.weatherforcast.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stranger on 2016/5/11.
 */
public class NetInfoBean extends LocalInfoBean implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.liveIcon);
        dest.writeString(this.liveWeather);
        dest.writeString(this.liveWindPower);
        dest.writeString(this.liveWindDirection);
        dest.writeString(this.liveTemperature);
        dest.writeString(this.liveHumidity);
        dest.writeString(this.liveAirDesc);
        dest.writeStringArray(this.threeHoursIcon);
        dest.writeStringArray(this.threeHoursWindPower);
        dest.writeStringArray(this.threeHoursValue);
        dest.writeStringArray(this.threeHoursTemperature);
        dest.writeStringArray(this.sixDayDayTemperature);
        dest.writeStringArray(this.sixDayNightTemperature);
        dest.writeStringArray(this.sixDayWeather);
        dest.writeStringArray(this.sixDayWindPower);
        dest.writeStringArray(this.sixDayWeekday);
        dest.writeStringArray(this.suggestionDesc);
    }

    public NetInfoBean() {
    }

    protected NetInfoBean(Parcel in) {
        this.liveIcon = in.readString();
        this.liveWeather = in.readString();
        this.liveWindPower = in.readString();
        this.liveWindDirection = in.readString();
        this.liveTemperature = in.readString();
        this.liveHumidity = in.readString();
        this.liveAirDesc = in.readString();
        this.threeHoursIcon = in.createStringArray();
        this.threeHoursWindPower = in.createStringArray();
        this.threeHoursValue = in.createStringArray();
        this.threeHoursTemperature = in.createStringArray();
        this.sixDayDayTemperature = in.createStringArray();
        this.sixDayNightTemperature = in.createStringArray();
        this.sixDayWeather = in.createStringArray();
        this.sixDayWindPower = in.createStringArray();
        this.sixDayWeekday = in.createStringArray();
        this.suggestionDesc = in.createStringArray();
    }

    public static final Parcelable.Creator<NetInfoBean> CREATOR = new Parcelable.Creator<NetInfoBean>() {
        @Override
        public NetInfoBean createFromParcel(Parcel source) {
            return new NetInfoBean(source);
        }

        @Override
        public NetInfoBean[] newArray(int size) {
            return new NetInfoBean[size];
        }
    };
}
