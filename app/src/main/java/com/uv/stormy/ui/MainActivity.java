package com.uv.stormy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.uv.stormy.R;
import com.uv.stormy.weather.Current;
import com.uv.stormy.weather.Day;
import com.uv.stormy.weather.Forecast;
import com.uv.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";

    /*
    * Define a request code to send to Google Play services
    * This code is returned in Activity.onActivityResult
    */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private Forecast mForecast;

    // Bind a field to the view for the specified ID. The view will automatically be cast to the field type.
    @BindView(R.id.timeLabel)
    TextView mTimeLabel;
    @BindView(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @BindView(R.id.humidityValue)
    TextView mHumidityValue;
    @BindView(R.id.precipValue)
    TextView mPrecipValue;
    @BindView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @BindView(R.id.iconImageView)
    ImageView mIconImageView;
    @BindView(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.locationLabel)
    TextView mLocationlabel;
    @BindView(R.id.dailyButton)
    Button mDailyButton;
    private static final String API_KEY = "f001bb783cc27374e9f473499f3b196f";
    /* ButterKnife finds annotations and generates the code we need for binding on the fly */

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

        // TODO на данный момент просто напрямую вызывает getForecast, но не проверяет и не получает новую локацию
        // TODO добавить запрос новой локации. Если не получилось, вывести TOAST и юзать старую.
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast();
            }
        });


        // // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this) // MainActivity will handle connection stuff
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API) // adds the LocationServices API endpoint from GooglePlayServices
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // as accurate a location as possible.
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds. Fastest interval at which our app will receive updates.
        /* This can be lower than the interval we set ourselves in case other apps are
        requesting location updates from Google Play Services. When that happens,
        our app can passively listen to any location updates, which doesn’t cost any extra power. */

//        getForecast(latitude, longtitude);

        Log.d(TAG, "Main UI code is running!");

    }

    private void getForecast() {
        // Используем параметр Locale.ROOT, чтобы десятичный сепаратор был точкой, а не запятой.
        String forecastUrl = String.format(Locale.ROOT, "https://api.forecast.io/forecast/%s/%f,%f", API_KEY, mLocation.getLatitude(), mLocation.getLongitude());

        // TODO perform geolocation service checks here
        // Check if network is available
        if (isNetworkAvailable()) {
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
            // Asynchronous GET
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
                    Log.d(TAG, jsonData);
                    try {
                        Log.d(TAG, response.toString());
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
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

        } else {
            // Network is unavailable
            Toast.makeText(MainActivity.this, R.string.network_unavailable_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText((int) current.getTemperature() + "");
        mSummaryLabel.setText(current.getSummary());
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mLocationlabel.setText(current.getTimeZone()
                .replaceAll("/", ", ")
                .replaceAll("_", " "));

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);

    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    // FIXME use Jackson
    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setTimezone(timezone);
            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        // JSON representation. editable and readable
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();

        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.i(TAG, "From JSON: " + timezone + "\nWeather: " + current.toString() + "\nFormatted time: " + current.getFormattedTime());

        return current;
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
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        // Show error dialog
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @OnClick(R.id.dailyButton)// method that executes when button with given id is clicked
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        // pass array of days forecast
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
        startActivity(intent);
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
        startActivity(intent);
    }


    /* Google Play Services */

    // When our client finally connects to the location services, the onConnected() method will be called.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");


        // If the device is running Android 6.0 or higher, and your app's target SDK is 23 or higher,
        // the app has to list the permissions in the manifest and request those permissions at run time.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG,"checkSelfPermission() failed");
            return;
        }
        // TODO prompt user to turn on geolocation somewhere
        // Request last known location
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Check if there is last know location
        if (location == null) {
            // Blank for a moment...
            Log.d(TAG,"Last location unknown. Requesting new location from "+LocationManager.GPS_PROVIDER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        else {
            Log.d(TAG,"Latitude: "+location.getLatitude()+"\nLongtitude: "+location.getLongitude());
            handleNewLocation(location);
        };
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLocation=location;
        getForecast();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    // gets called every time a new location is detected by Google Play Services
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    /* Activity Lifecycle */

    @Override
    protected void onResume() {
        super.onResume();
        // connect to google play services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disconnect everytime activity is overlapped
        // Removing Updates When Finished
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}
