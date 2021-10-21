package tourGuide.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.RewardCentralProxy;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    private final GpsUtilProxy gpsUtil;

    private final RewardCentralProxy rewardCentral;
	// proximity in miles
    private final int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	@Autowired
	public RewardsService(GpsUtilProxy gpsUtil, RewardCentralProxy rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(User user) {
		List<VisitedLocationTourGuide> userLocations = new CopyOnWriteArrayList(user.getVisitedLocations());
		List<AttractionTourGuide> attractions = gpsUtil.getAttractions().getBody();
		
		for(VisitedLocationTourGuide visitedLocation : userLocations) {
			assert attractions != null;
			for(AttractionTourGuide attraction : attractions) {
				if(user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user.getUserId())));
					}
				}
			}
		}
	}
	
	public boolean isWithinAttractionProximity(AttractionTourGuide attraction, LocationTourGuide location) {
		return getDistance(attraction, location) <= attractionProximityRange;
	}
	
	private boolean nearAttraction(VisitedLocationTourGuide visitedLocation, AttractionTourGuide attraction) {
		return getDistance(attraction, visitedLocation.location) <= Double.valueOf(proximityBuffer);
	}
	
	public int getRewardPoints(AttractionTourGuide attraction, UUID userId) {
		return rewardCentral.getAttractionRewardPoints(attraction.attractionId.toString(), userId.toString()).getBody();
	}
	
	public double getDistance(AttractionTourGuide attractionTourGuide, LocationTourGuide locationTourGuide) {
        double lat1 = Math.toRadians(attractionTourGuide.latitude);
        double lon1 = Math.toRadians(attractionTourGuide.longitude);
        double lat2 = Math.toRadians(locationTourGuide.latitude);
        double lon2 = Math.toRadians(locationTourGuide.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
