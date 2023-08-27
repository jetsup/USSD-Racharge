package com.jetsup.ussdracharge;

import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_INFORMATION;
import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_PRESENT;
import static com.jetsup.ussdracharge.custom.SharedPreferenceKeys.PREFERENCE_ACCENT_COLOR;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jetsup.ussdracharge.adapters.ISPAdapter;

import java.io.File;
import java.util.List;
import java.util.Objects;

@ExperimentalGetImage
public class MainActivity extends AppCompatActivity {
    private static final int PHONE_STATE_PERMISSION = 2;
    private static ActionBar actionBar;
    private static Window window;
    private final int CALL_PERMISSION_REQUEST_CODE = 1;
    SharedPreferences settingsPreferences;
    List<SubscriptionInfo> activeSubscriptionInfoList;
    ISPAdapter ispAdapter;
    boolean sameCarrier;
    int simCardsDetected;
    int accentColor;
    String[] simCards = {};
    RecyclerView mainRecyclerView;
    File myDirectory;

    private static void setActionBarColor(int accentColor) {
        actionBar.setBackgroundDrawable(new ColorDrawable(accentColor));
        window.setStatusBarColor(accentColor);
    }

    private void requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Allow Permission")
                    .setMessage("This permissions are required")
                    .setPositiveButton("OK", (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                                    CALL_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, CALL_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "The Permission has been successfully granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        actionBar = Objects.requireNonNull(getSupportActionBar());
        window = getWindow();
        accentColor = settingsPreferences.getInt(PREFERENCE_ACCENT_COLOR, getResources().getColor(R.color.purple_700, null));
        setActionBarColor(accentColor);

        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.ussd);
        // Manage permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            requestCallPermission();
        }
        // Check Sim cards
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        simCardsDetected = activeSubscriptionInfoList.size();
        if (simCardsDetected > 0) {
            simCards = new String[simCardsDetected];
            sameCarrier = simCardsDetected == 2 && activeSubscriptionInfoList.get(0).getCarrierName().equals(activeSubscriptionInfoList.get(1).getCarrierName());
            for (int i = 0; i < simCardsDetected; i++) {
                simCards[i] = activeSubscriptionInfoList.get(i).getSimSlotIndex()
                        + " " + activeSubscriptionInfoList.get(i).getCarrierName() // use as key in the hashmap
                        + " " + activeSubscriptionInfoList.get(i).getIconTint();
            }
        }
        // Start initialization here
        mainRecyclerView = findViewById(R.id.ispRecyclerView);
        if (simCardsDetected > 0) {
            ispAdapter = new ISPAdapter(MainActivity.this, activeSubscriptionInfoList, sameCarrier);
        } else {
            ispAdapter = new ISPAdapter(MainActivity.this);
        }
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainRecyclerView.setAdapter(ispAdapter);
        Button btnOpenCam = findViewById(R.id.btnOpenCamera);
        btnOpenCam.setOnClickListener(v -> {
            Intent scanIntent = new Intent(MainActivity.this, ScannerActivity.class);
            if (activeSubscriptionInfoList.size() > 0) {
                String[] simCards = new String[activeSubscriptionInfoList.size()];
                for (int i = 0; i < activeSubscriptionInfoList.size(); i++) {
                    simCards[i] = activeSubscriptionInfoList.get(i).getSimSlotIndex() + "" + activeSubscriptionInfoList.get(i).getCarrierName();
                }
                scanIntent.putExtra(SIM_CARD_INFORMATION, simCards);
                scanIntent.putExtra(SIM_CARD_PRESENT, true);
            }
            startActivity(scanIntent);
        });

        myDirectory = new File(Objects.requireNonNull(getExternalCacheDir().getParentFile()).getPath() + "/Kenya USSD/");
        if (!myDirectory.exists()) {
            if (!myDirectory.mkdir()) {
                Toast.makeText(this, "The folder was not created", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        accentColor = settingsPreferences.getInt(PREFERENCE_ACCENT_COLOR, getResources().getColor(R.color.purple_700, null));
        setActionBarColor(accentColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
