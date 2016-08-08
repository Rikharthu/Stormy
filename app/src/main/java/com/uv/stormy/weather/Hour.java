package com.uv.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

// Hourly weather
public class Hour implements Parcelable {

    private long mTime;
    private String mSummary;
    private double mTemperature;
    private String mIcon;
    private String mTimezone;

    public Hour() {}

/* Getters and Setters */

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperature() {
        return (int)(Math.round((mTemperature -32)*(5/9f)));
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getHour(){
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        Date date = new Date(mTime*1000);
        return formatter.format(date);
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }

    /* Parcelable */

    protected Hour(Parcel in) {
        mTime = in.readLong();
        mSummary = in.readString();
        mTemperature = in.readDouble();
        mIcon = in.readString();
        mTimezone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperature);
        dest.writeString(mIcon);
        dest.writeString(mTimezone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Hour> CREATOR = new Parcelable.Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel in) {
            return new Hour(in);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };

}
