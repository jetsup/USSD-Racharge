package com.jetsup.ussdracharge.custom;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MyMapOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> overlayItems = new ArrayList<>();
    private MapView mapView;

//    public MyMapOverlay(Drawable drawable, MapView mapView){
//        super(boundCenterBottom(drawable));
//        this.mapView=mapView;
//
//    }

    public MyMapOverlay(Drawable pDefaultMarker, MapView mapView) {
        super(pDefaultMarker);
        this.mapView = mapView;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return null;
    }

    public void addOverlayItem(OverlayItem item) {
        overlayItems.add(item);
        populate();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
        return false;
    }
}
