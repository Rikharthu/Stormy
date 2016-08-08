package com.uv.stormy.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.uv.stormy.R;
import com.uv.stormy.adapters.HourAdapter;
import com.uv.stormy.weather.Hour;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {

    private Hour[] mHours;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        Parcelable[] parcelables=intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        // (original, length, class of the new array)
        mHours = Arrays.copyOf(parcelables,parcelables.length,Hour[].class);

        HourAdapter adapter = new HourAdapter(this,mHours);
        mRecyclerView.setAdapter(adapter);

        // LayoutManager determines when list items are no longer visible and can be reused
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

    }
}
