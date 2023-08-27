package com.jetsup.ussdracharge.fragments;

import static com.jetsup.ussdracharge.custom.ISPConstants.M_TAG;
import static com.jetsup.ussdracharge.custom.SharedPreferenceKeys.PREFERENCE_ACCENT_COLOR;
import static com.jetsup.ussdracharge.custom.SharedPreferenceKeys.PREFERENCE_THEME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.jetsup.ussdracharge.R;
import com.jetsup.ussdracharge.custom.PreferenceColorDialog;

import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsFragment extends PreferenceFragmentCompat {
    int currentColor;
    Context context;
    SharedPreferences settingsPreference;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.main_setting_screen, rootKey);
        context = this.requireContext();
        settingsPreference = Objects.requireNonNull(getPreferenceManager().getSharedPreferences());
        currentColor = settingsPreference.getInt(PREFERENCE_ACCENT_COLOR, R.color.teal_200);

        Objects.requireNonNull(ResourcesCompat.getDrawable(context.getResources(), R.drawable.accent_color_viewer, null))
                .setTint(currentColor);
        Objects.requireNonNull(ResourcesCompat.getDrawable(context.getResources(), R.drawable.moon, null))
                .setTint(getResources().getColor(R.color.grey, null));
        Preference pref = Objects.requireNonNull(getPreferenceManager().getPreferenceScreen().findPreference(PREFERENCE_THEME));
        pref.setIcon(R.drawable.sun);
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        Log.w(M_TAG, "onDisplayPreferenceDialog: " + preference.getKey());
        if (preference instanceof PreferenceColorDialog) {
            new AmbilWarnaDialog(this.getContext(), currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    settingsPreference.edit().putInt(PREFERENCE_ACCENT_COLOR, color).apply();
                    Objects.requireNonNull(ResourcesCompat
                                    .getDrawable(SettingsFragment.this.requireContext().getResources(),
                                            R.drawable.accent_color_viewer, null))
                            .setTint(color);
                    preference.setIcon(R.drawable.accent_color_viewer);
                    currentColor = color;
                }
            }).show();
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
