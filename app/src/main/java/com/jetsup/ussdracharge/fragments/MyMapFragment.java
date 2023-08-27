package com.jetsup.ussdracharge.fragments;

import static com.jetsup.ussdracharge.custom.ISPConstants.M_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;
import com.jetsup.ussdracharge.R;

public class MyMapFragment extends SupportMapFragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        Log.w(M_TAG, "MapFragment: " + (v));
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
