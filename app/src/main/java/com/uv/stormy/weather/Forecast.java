package com.uv.stormy.weather;

import com.uv.stormy.R;

// Combination
public class Forecast {
    private Current mCurrent;
    private Hour[] mHourlyForecast;
    // Daily forecase for the upcoming week
    private Day[]  mDailyForecast;

    // Static helper method
    public static int getIconId(String iconString){
        // clear-day, clear-night, rain, snow, sleet, wind, fog,
        // cloudy, partly-cloudy-day, or partly-cloudy-night

        int iconId;
        //#F6AA0E
        // Get according drawable ids, depending on icon from JSON
        switch(iconString){
            case "clear-day":
                iconId= R.drawable.clear_day;
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


    /* Getters and Setters */
    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }
}