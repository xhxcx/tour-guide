package tourGuide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {

	@Autowired
	private GpsUtilProxy gpsUtil;

	private final RewardsService rewardsService = new RewardsService(new RewardCentral());
	private final TourGuideService tourGuideService = new TourGuideService(rewardsService);

	@Test
	public void userGetRewards() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionTourGuide attraction = Objects.requireNonNull(gpsUtil.getAttractions().getBody()).get(0);
		user.addToVisitedLocations(new VisitedLocationTourGuide(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();

		assertEquals(1, userRewards.size());
	}
	
	@Test
	public void isWithinAttractionProximity() {
		AttractionTourGuide attraction = Objects.requireNonNull(gpsUtil.getAttractions().getBody()).get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	

	@Test
	public void nearAllAttractions() {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(Objects.requireNonNull(gpsUtil.getAttractions().getBody()).size(), userRewards.size());
	}
	
}
