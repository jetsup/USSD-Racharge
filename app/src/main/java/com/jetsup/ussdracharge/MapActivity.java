package com.jetsup.ussdracharge;

import static com.jetsup.ussdracharge.custom.ISPConstants.M_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jetsup.ussdracharge.fragments.MyMapFragment;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        Log.w(M_TAG, "mpf: " + (mapFragment == null));
        MyMapFragment mpF = new MyMapFragment();
        Log.w(M_TAG, "mpfClass: " + (mpF == null));

        Fragment fragment = new Fragment();
        int commit = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapFragmentFrame, mpF)
                .commit();
        Log.w(M_TAG, "fr: " + (fragment == null) + " <> " + commit);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        updateGPS();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                Log.w(M_TAG, "Lo: " + location.getLongitude() + "deg" +
                        "\nLa: " + location.getLatitude() + "deg" +
                        "\nBb: " + location.hasBearing() +
                        "\nB: " + location.getBearing() + "deg" +
                        "\nS: " + location.getSpeed() + "m/s" +
                        "\nSa: " + location.getSpeedAccuracyMetersPerSecond() + "M/S" +
                        "\nPr: " + location.getProvider() +
                        "\nEx: " + location.getExtras().toString() +
                        " \n\nALL: " + location);
                Toast.makeText(MapActivity.this,
                        "Lo: " + location.getLongitude() +
                                "\nLa: " + location.getLatitude() +
                                "\nB: " + location.hasBearing() +
                                "\nBe: " + location.getBearing(),
                        Toast.LENGTH_SHORT).show();
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(lastLocation).title("Last Seen"));
        });
    }
}
