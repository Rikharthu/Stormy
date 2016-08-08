package com.uv.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.uv.stormy.R;
import com.uv.stormy.adapters.DayAdapter;
import com.uv.stormy.weather.Day;

import java.lang.reflect.Array;
import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        // receive daily forecast from previous activity
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        // data, length, class
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);


        DayAdapter adapter = new DayAdapter(this, mDays);
        getListView().setAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        /*  l - listView that was clicked
            v - specific item that was clicked
            position - numerical index of the item in the list
            id - something  */
        super.onListItemClick(l, v, position, id);

        // prepare message
        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax()+"";
        String message = String.format("On %s the high will be %s and it will be %s",
                dayOfTheWeek,highTemp,conditions);

        Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
