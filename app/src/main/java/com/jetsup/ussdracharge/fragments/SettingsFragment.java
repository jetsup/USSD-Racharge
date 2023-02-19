package com.jetsup.ussdracharge.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.jetsup.ussdracharge.R;
import com.jetsup.ussdracharge.custom.PreferenceColorDialog;

import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsFragment extends PreferenceFragmentCompat {
    final String TAG = "MyTag";
    int currentColor;
    SharedPreferences settingsPreference;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.main_setting_screen, rootKey);
        settingsPreference = Objects.requireNonNull(getPreferenceManager().getSharedPreferences());
        currentColor = settingsPreference.getInt("accentColor", R.color.teal_200);
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        Log.w(TAG, "onDisplayPreferenceDialog: " + preference.getKey());
        if (preference instanceof PreferenceColorDialog) {
            new AmbilWarnaDialog(this.getContext(), currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    settingsPreference.edit().putInt("accentColor", color).apply();
                    currentColor = color;
                }
            }).show();
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
