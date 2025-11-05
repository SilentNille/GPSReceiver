package com.example.gpsreceiver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView altitudeTextView;
    private TextView speedTextView;
    private TextView accuracyTextView;
    private TextView bearingTextView;
    private TextView timeTextView;

    private LocationManager locationManager;
    private String currentProvider = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.latitude_text);
        longitudeTextView = findViewById(R.id.longitude_text);
        altitudeTextView = findViewById(R.id.altitude_text);
        speedTextView = findViewById(R.id.speed_text);
        accuracyTextView = findViewById(R.id.accuracy_text);
        bearingTextView = findViewById(R.id.bearing_text);
        timeTextView = findViewById(R.id.time_text);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (hasLocationPermission()) {
            requestLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.provider_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.gps_provider) {
            currentProvider = LocationManager.GPS_PROVIDER;
            Log.d(TAG, "Provider changed to: " + currentProvider);
            restartLocationUpdates();
            return true;
        } else if (itemId == R.id.network_provider) {
            currentProvider = LocationManager.NETWORK_PROVIDER;
            Log.d(TAG, "Provider changed to: " + currentProvider);
            restartLocationUpdates();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        float speed = location.getSpeed();
        float accuracy = location.getAccuracy();
        float bearing = location.getBearing();
        long time = location.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(time));

        latitudeTextView.setText(String.format("Breitengrad: %.6f", latitude));
        longitudeTextView.setText(String.format("Längengrad: %.6f", longitude));
        altitudeTextView.setText(String.format("Höhe: %.2f m", altitude));
        speedTextView.setText(String.format("Geschwindigkeit: %.2f km/h", speed * 3.6));

        accuracyTextView.setText(String.format("Genauigkeit: %.2f m", accuracy));
        bearingTextView.setText(String.format("Bewegungsrichtung: %.2f °", bearing));
        timeTextView.setText("Uhrzeit: " + formattedTime);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && hasLocationPermission()) {
            requestLocationUpdates();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void requestLocationUpdates() {
        if (!hasLocationPermission()) {
            return;
        }
        locationManager.requestLocationUpdates(currentProvider, 0, 0, this);
    }

    private void restartLocationUpdates() {
        locationManager.removeUpdates(this);
        requestLocationUpdates();
    }
}
