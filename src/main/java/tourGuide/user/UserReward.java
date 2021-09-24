package tourGuide.user;

import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;

public class UserReward {

	public final VisitedLocationTourGuide visitedLocation;
	public final AttractionTourGuide attraction;
	private int rewardPoints;
	public UserReward(VisitedLocationTourGuide visitedLocation, AttractionTourGuide attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocationTourGuide visitedLocation, AttractionTourGuide attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}
	
}
