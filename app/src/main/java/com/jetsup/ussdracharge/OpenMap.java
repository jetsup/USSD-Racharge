package com.jetsup.ussdracharge;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.Arrays;

public class OpenMap extends AppCompatActivity {
    final String TAG = "MyTag";
    private final int LOCATION_REQUEST_CODE = 100;
    ItemizedOverlay<OverlayItem> overlayItems;
    MapView mapView;
    //    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean hasLocationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing the library configurations
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        // set mapView layout
        setContentView(R.layout.activity_open_map);
        mapView = findViewById(R.id.mapView);
        // set mapView tile source
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        // get the last known user location
//        locationRequest = LocationRequest.create();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//        hasLocationPermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!permissionDenied()) {
            hasLocationPermission = true;
            updateLocation(); // if the user does not give permission, show default location
        }
    }

    private void updateLocation() {
        try {
            if (hasLocationPermission) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            // set the mapView initial zoom level and center point
                            mapView.getController().setZoom(15.0);
                            mapView.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                );
            } else {
                mapView.getController().setZoom(10.0);
                mapView.getController().setCenter(new GeoPoint(38.9, -9.6));
            }
        } catch (SecurityException ignore) {
        }
//        mapView.getController().setZoom(12.0);
//         set the mapView initial zoom level and center point
//        mapView.getController().setCenter(new GeoPoint(52.5, 13.4));
    }

    private boolean permissionDenied() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return true;
        } else {
            updateLocation();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult: " + requestCode + " <> " + Arrays.toString(permissions) + " <> " + Arrays.toString(grantResults));
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
