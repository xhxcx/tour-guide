package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import com.jsoniter.output.JsonStream;
import org.junit.Before;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.ClosestAttractionDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {
	private final GpsUtil gpsUtil = new GpsUtil();
	private final RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
	private final User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

	@Before
	public void setUp() {
		InternalTestHelper.setInternalUserNumber(0);
	}

	@Test
	public void getUserLocation() {
		//TODO pk on mock pas le visitedLocation attendu puis spy sur trackUserLocation ?
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		assertEquals(visitedLocation.userId, user.getUserId());
	}
	
	@Test
	public void addUser() {
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<ClosestAttractionDTO> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		assertEquals(5, attractions.size());
	}

	public void getTripDeals() {

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		assertEquals(10, providers.size());
	}
}
