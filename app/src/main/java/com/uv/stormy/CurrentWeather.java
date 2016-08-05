package com.uv.stormy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/** Our model object
 *
 */
public class CurrentWeather {

    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;


    public String getFormattedTime(){
        // h - hours, m - minutes, a - AM or PM
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        // specify the timezone
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));

        // multiply UNIX time by 1000, since day accepts milliseconds!
        Date date = new Date(mTime*1000);

        // get formatted date string
        String timeString = formatter.format(date);

        return timeString;
    }

    // FIXME переделать в анимированные Skycons
    public int getIconId(){
        // clear-day, clear-night, rain, snow, sleet, wind, fog,
        // cloudy, partly-cloudy-day, or partly-cloudy-night

        int iconId;
        //#F6AA0E
        // Get according drawable ids, depending on icon from JSON
        switch(mIcon){
            case "clear-day":
                iconId=R.drawable.clear_day;
                break;
            case "clear-night":
                iconId=R.drawable.clear_night;
                break;
            case "rain":
                iconId=R.drawable.rain;
                break;
            case "snow":
                iconId=R.drawable.snow;
                break;
            case "sleet":
                iconId=R.drawable.sleet;
                break;
            case "wind":
                iconId=R.drawable.wind;
                break;
            case "fog":
                iconId=R.drawable.fog;
                break;
            case "cloudy":
                iconId=R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId=R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId=R.drawable.cloudy_night;
                break;
            default:
                iconId=R.drawable.clear_day;
        }

        return iconId;
    }


    /* GETTERS AND SETTERS */

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public double getTemperature() {
        // Convert Fahrenheit to Celsius
        return (mTemperature -32)*(5/9f) ;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance *100;
        return (int)Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "mIcon='" + mIcon + '\'' +
                ", mTime=" + mTime +
                ", mTemperature=" + mTemperature +
                ", mHumidity=" + mHumidity +
                ", mPrecipChance=" + mPrecipChance +
                ", mSummary='" + mSummary + '\'' +
                ", mTimeZone='" + mTimeZone + '\'' +
                '}';
    }
}
