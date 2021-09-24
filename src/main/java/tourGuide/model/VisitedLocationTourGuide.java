package tourGuide.model;

import java.util.Date;
import java.util.UUID;

public class VisitedLocationTourGuide {
    public UUID userId;
    public LocationTourGuide location;
    public Date timeVisited;

    public VisitedLocationTourGuide() {
    }

    public VisitedLocationTourGuide(UUID userId, LocationTourGuide location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocationTourGuide getLocation() {
        return location;
    }

    public void setLocation(LocationTourGuide location) {
        this.location = location;
    }

    public Date getTimeVisited() {
        return timeVisited;
    }

    public void setTimeVisited(Date timeVisited) {
        this.timeVisited = timeVisited;
    }
}
