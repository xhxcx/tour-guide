package tourGuide.dto;

import gpsUtil.location.Location;

public class ClosestAttractionDTO {
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.

    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distance;
    private int rewardPointValue;

    public ClosestAttractionDTO(String attractionName, Location attractionLocation, Location userLocation, double distance, int rewardPointValue) {
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

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
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
