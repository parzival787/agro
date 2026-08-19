package com.example.agro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity {

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"; // URL for the weather API.
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY; // API key for the weather service, stored in BuildConfig.

    private TextView tvLocation, tvTemp, tvCondition, tvSuitability, tvAlerts; // Declares TextViews for displaying weather data and advice.
    private ProgressBar progressBar; // Declares a ProgressBar to indicate loading.
    private Spinner cropSpinner; // Declares a Spinner for crop selection.
    private FusedLocationProviderClient fusedLocationClient; // Declares a FusedLocationProviderClient to get the device's location.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is created.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 1. Initialize Views
        tvLocation = findViewById(R.id.tvLocation);
        tvTemp = findViewById(R.id.tvTemp);
        tvCondition = findViewById(R.id.tvCondition);
        tvSuitability = findViewById(R.id.tvSuitability);
        tvAlerts = findViewById(R.id.tvAlerts);
        progressBar = findViewById(R.id.progressBar);
        cropSpinner = findViewById(R.id.cropSpinner);

        // 2. Populate Spinner
        List<String> crops = new ArrayList<>();
        crops.add(getString(R.string.crop_tomato));
        crops.add(getString(R.string.crop_rice));
        crops.add(getString(R.string.crop_wheat));
        crops.add(getString(R.string.crop_maize));
        crops.add(getString(R.string.crop_cotton));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropSpinner.setAdapter(adapter);

        // 3. Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 4. Get Location & Weather
        getLocation();

        // 5. Spinner Listener
        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tvCondition.getTag() != null) {
                    String selectedCrop = parent.getItemAtPosition(position).toString();
                    try {
                        JSONObject json = new JSONObject(tvCondition.getTag().toString());
                        double temp = json.getJSONObject("main").getDouble("temp");
                        double humidity = json.getJSONObject("main").getDouble("humidity");
                        String description = json.getJSONArray("weather").getJSONObject(0).getString("main");
                        generateAgriAdvice(temp, humidity, description, selectedCrop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                fetchWeather(location.getLatitude(), location.getLongitude());
            } else {
                tvLocation.setText(getString(R.string.location_not_found));
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchWeather(double lat, double lon) {
        String url = WEATHER_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, getString(R.string.error_fetching_weather), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String cityName = json.getString("name");
                        JSONObject main = json.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        double humidity = main.getDouble("humidity");
                        String description = json.getJSONArray("weather").getJSONObject(0).getString("main");

                        runOnUiThread(() -> {
                            tvLocation.setText(cityName);
                            tvTemp.setText(String.format("%.1f°C", temp));
                            tvCondition.setText(description);
                            tvCondition.setTag(responseData);
                            String selectedCrop = cropSpinner.getSelectedItem().toString();
                            generateAgriAdvice(temp, humidity, description, selectedCrop);
                            progressBar.setVisibility(View.GONE);
                        });

                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(WeatherActivity.this, getString(R.string.error_parsing_weather), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(WeatherActivity.this, getString(R.string.error_fetching_weather), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    // --- THE INTELLIGENT CROP ADVISOR LOGIC ---
    private void generateAgriAdvice(double temp, double humidity, String condition, String crop) {
        StringBuilder cropSuitability = new StringBuilder();
        StringBuilder alertsAndTasks = new StringBuilder();

        // --- CROP SPECIFIC ADVICE ---
        if (crop.equals(getString(R.string.crop_tomato))) {
            if (temp > 32) {
                alertsAndTasks.append(getString(R.string.tomato_heat_alert));
            } else if (temp < 15) {
                alertsAndTasks.append(getString(R.string.tomato_cold_alert));
            }
            if (humidity > 80 && (condition.toLowerCase().contains("rain") || condition.toLowerCase().contains("drizzle"))) {
                alertsAndTasks.append(getString(R.string.tomato_humidity_alert));
            }
        } else if (crop.equals(getString(R.string.crop_rice))) {
             if (temp > 35) {
                alertsAndTasks.append(getString(R.string.rice_heat_alert));
            } else if (temp < 20) {
                alertsAndTasks.append(getString(R.string.rice_cold_alert));
            }
            if (condition.toLowerCase().contains("rain")) {
                cropSuitability.append(getString(R.string.rice_transplanting_advice));
            } else {
                alertsAndTasks.append(getString(R.string.rice_water_advice));
            }
        } else if (crop.equals(getString(R.string.crop_wheat))) {
            if (temp > 25) {
                alertsAndTasks.append(getString(R.string.wheat_heat_alert));
            } else if (temp < 5) {
                alertsAndTasks.append(getString(R.string.wheat_frost_alert));
            }
            if (humidity > 70 && (condition.toLowerCase().contains("rain") || condition.toLowerCase().contains("drizzle"))) {
                alertsAndTasks.append(getString(R.string.wheat_humidity_alert));
            }
        } else if (crop.equals(getString(R.string.crop_maize))) {
             if (temp > 35) {
                alertsAndTasks.append(getString(R.string.maize_heat_alert));
            } else if (temp < 10) {
                alertsAndTasks.append(getString(R.string.maize_cold_alert));
            }
            if (condition.toLowerCase().contains("rain")) {
                alertsAndTasks.append(getString(R.string.maize_drainage_advice));
            }
        } else if (crop.equals(getString(R.string.crop_cotton))) {
             if (temp > 35) {
                alertsAndTasks.append(getString(R.string.cotton_heat_alert));
            } else if (temp < 20) {
                alertsAndTasks.append(getString(R.string.cotton_cold_alert));
            }
            if (humidity > 70 && (condition.toLowerCase().contains("rain") || condition.toLowerCase().contains("drizzle"))) {
                alertsAndTasks.append(getString(R.string.cotton_humidity_alert));
            }
        }

        // --- THE FIX: ADD DEFAULT MESSAGES IF NO RISKS ARE DETECTED ---
        if (cropSuitability.length() == 0) {
            cropSuitability.append(getString(R.string.stable_conditions_advice));
        }

        if (alertsAndTasks.length() == 0) {
            alertsAndTasks.append(getString(R.string.no_immediate_risks));
        }

        // Set the text
        tvSuitability.setText(cropSuitability.toString().trim());
        tvAlerts.setText(alertsAndTasks.toString().trim());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
