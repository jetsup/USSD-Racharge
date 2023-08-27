package com.jetsup.ussdracharge;

import static com.jetsup.ussdracharge.custom.ISPConstants.M_TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OpenMap extends AppCompatActivity {
    private final int LOCATION_REQUEST_CODE = 100;
    ItemizedOverlay<OverlayItem> overlayItems;
    MapView mapView;
    MapController mapController;
    //    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean hasLocationPermission;
    private List<Overlay> overlayItemList = new ArrayList<>();

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
        mapController = (MapController) mapView.getController();
        // get the last known user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (!permissionDenied()) {
            hasLocationPermission = true;
            updateLocation(); // if the user does not give permission, show default location
        }
    }

    public void addMarker(GeoPoint loc, boolean myLocation) {
        Marker marker = new Marker(mapView);
        marker.setPosition(loc);
        if (myLocation) {
            marker.setTitle("Your last known location");
        } else {
            marker.setTitle("Location of interest");
        }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable redLocationMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_location_on_24, null);
        Objects.requireNonNull(redLocationMarker).setTint(getColor(R.color.airtel_red));
        marker.setIcon(redLocationMarker);
        mapView.getOverlays().clear();
        overlayItemList.add(marker);
        for (Overlay overlay : overlayItemList) {
            mapView.getOverlays().add(overlay);
        }
        mapView.postInvalidate();
        if (myLocation) {
            mapController.animateTo(loc, 18.0, 1000L);
        }
    }

    private void updateLocation() {
        try {
            if (hasLocationPermission) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            // set the mapView initial zoom level and center point
                            addMarker(new GeoPoint(location.getLatitude(), location.getLongitude()), true);
                            addMarker(new GeoPoint(location.getLatitude() + 0.3, location.getLongitude() + 0.2), false);
//                            mapView.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
//                            mapView.getController().setZoom(20.0);
                            Log.d(M_TAG, "updateLocation: has permission");
//                            mapView.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                );
            } else {
                mapView.getController().setZoom(10.0);
                mapView.getController().setCenter(new GeoPoint(38.9, -9.6));
                Log.d(M_TAG, "updateLocation: has no permission");
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
        Log.w(M_TAG, "onRequestPermissionsResult: " + requestCode + " <> " + Arrays.toString(permissions) + " <> " + Arrays.toString(grantResults));
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
