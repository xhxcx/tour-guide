package tourGuide.dto;

import gpsUtil.location.Location;

public class UserCurrentLocationDTO {

    private String userId;
    private Location userLastLocation;

    public UserCurrentLocationDTO(String userId, Location userLastLocation) {
        this.userId = userId;
        this.userLastLocation = userLastLocation;
    }
}
