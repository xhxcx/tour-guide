package tourGuide.dto;

import tourGuide.model.LocationTourGuide;

public class UserCurrentLocationDTO {

    private String userId;
    private LocationTourGuide userLastLocation;

    public UserCurrentLocationDTO(String userId, LocationTourGuide userLastLocation) {
        this.userId = userId;
        this.userLastLocation = userLastLocation;
    }
}
