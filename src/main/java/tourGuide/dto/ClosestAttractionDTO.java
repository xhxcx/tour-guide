package tourGuide.dto;

import tourGuide.model.LocationTourGuide;

public class ClosestAttractionDTO {
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.

    private String attractionName;
    private LocationTourGuide attractionLocation;
    private LocationTourGuide userLocation;
    private double distance;
    private int rewardPointValue;

    public ClosestAttractionDTO(String attractionName, LocationTourGuide attractionLocation, LocationTourGuide userLocation, double distance, int rewardPointValue) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distance = distance;
        this.rewardPointValue = rewardPointValue;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public LocationTourGuide getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(LocationTourGuide attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public LocationTourGuide getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LocationTourGuide userLocation) {
        this.userLocation = userLocation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRewardPointValue() {
        return rewardPointValue;
    }

    public void setRewardPointValue(int rewardPointValue) {
        this.rewardPointValue = rewardPointValue;
    }
}
