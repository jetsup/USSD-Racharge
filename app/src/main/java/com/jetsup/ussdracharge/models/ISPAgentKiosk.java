package com.jetsup.ussdracharge.models;

public class ISPAgentKiosk {
    double longitude, latitude;
    String businessName, ownerName, nearestTown, nearestLandmark;

    public ISPAgentKiosk(double longitude, double latitude, String businessName, String ownerName,
                         String nearestTown, String nearestLandmark) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.businessName = businessName;
        this.ownerName = ownerName;
        this.nearestTown = nearestTown;
        this.nearestLandmark = nearestLandmark;
    }

    public String getNearestLandmark() {
        return nearestLandmark;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getNearestTown() {
        return nearestTown;
    }
}
