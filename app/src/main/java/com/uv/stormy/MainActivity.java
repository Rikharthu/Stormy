package com.uv.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG =MainActivity.class.getSimpleName();

    // Bind a field to the view for the specified ID. The view will automatically be cast to the field type.
    @BindView(R.id.timeLabel)       TextView    mTimeLabel;
    @BindView(R.id.temperatureLabel)TextView    mTemperatureLabel;
    @BindView(R.id.humidityValue)   TextView    mHumidityValue;
    @BindView(R.id.precipValue)     TextView    mPrecipValue;
    @BindView(R.id.summaryLabel)    TextView    mSummaryLabel;
    @BindView(R.id.iconImageView)   ImageView   mIconImageView;
    @BindView(R.id.refreshImageView)ImageView   mRefreshImageView;
    @BindView(R.id.progressBar)     ProgressBar mProgressBar;
    @BindView(R.id.locationLabel)   TextView    mLocationlabel;
    /* ButterKnife finds annotations and generates the code we need for binding on the fly */


    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // no need to get references to views this way anymore
        // we use ButterKnife annotations
        // mTemperatureLabel = (TextView)findViewById(R.id.temperatureLabel);

        // BindView annotated fields and methods in the specified Activity
        ButterKnife.bind(this);
        // Грубо говоря, здесь будет сгенерирован код типа:
        // mTemperatureLabel = (TextView)findViewById(R.id.temperatureLabel);

        mProgressBar.setVisibility(View.INVISIBLE);

        // FIXME make dynamic
        final double latitude= 56.9496;
        final double longtitude= 24.1052;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast(latitude, longtitude);
            }
        });

        getForecast(latitude, longtitude);

        Log.d(TAG,"Main UI code is running!");

    }

    private void getForecast(double latitude, double longtitude) {
        String apiKey="f001bb783cc27374e9f473499f3b196f";
        String forecastUrl = String.format("https://api.forecast.io/forecast/%s/%f,%f",apiKey,latitude,longtitude);

        // Check if network is available
        if(isNetworkAvailable()) {
            toggleRefresh();

            // create OkHttpClient
            OkHttpClient client = new OkHttpClient();
            // build a request that will be sent to the server
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            // A call is a request that has been prepared for execution
            // Prepares the request to be executed at some point in the future
            Call call = client.newCall(request);

            // Download a file(http response) on a worker thread, and get called back when the response is readable.
            // in other words put this call in a queue, which will be executed in a background (probable after some time)
            // Asynchronous get
            call.enqueue(new Callback() { // Class, which methods will be executed later, when call executes
                // Called when the request could not be executed due to cancellation, a connectivity problem or timeout
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    Log.e(TAG, "Exception caught by onFailure() : ", e);
                    alertUserAboutError();
                }

                // Called when the HTTP response was successfully returned by the remote server
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    // get 'body' from the http response
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    try {
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);

                            // Only UI (main) can update UI. Currently this method is called from async thread (call.enqueue....)
                            // Runs the specified action on the UI thread. Not immediately
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            // alert the user about the error
                            alertUserAboutError();
                        }
                    } catch (Exception e) { // FIXME should be IOException
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });

        }else{
            // Network is unavailable
            Toast.makeText(MainActivity.this, R.string.network_unavailable_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText((int)mCurrentWeather.getTemperature()+"");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mHumidityValue.setText(mCurrentWeather.getHumidity()+"");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance()+"%");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime()+" it will be");
        mLocationlabel.setText(mCurrentWeather.getTimeZone()
                .replaceAll("/",", " )
                .replaceAll("_"," "));

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        // JSON representation. editable and readable
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.i(TAG, "From JSON: "+timezone+"\nWeather: "+currentWeather.toString()+"\nFormatted time: "+currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        // Get reference to connectivity service
        // Class that answers queries about the state of network connectivity.
        // It also notifies applications when network connectivity changes.
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // This method requires the caller to hold the permission ACCESS_NETWORK_STATE. (manifest)
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo !=null && networkInfo.isConnected()){
            isAvailable=true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        // Show error dialog
        dialog.show(getFragmentManager(),"error_dialog");
    }
}
