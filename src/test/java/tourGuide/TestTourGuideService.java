package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.ClosestAttractionDTO;
import tourGuide.dto.UserPreferencesDTO;
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
		InternalTestHelper.setInternalUserNumber(1);
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

	@Test
	public void getTripDeals() {

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		assertEquals(5, providers.size());
	}

	@Test
	public void getAllCurrentLocations() {
		List<User> userList = tourGuideService.getAllUsers();
		User firstUser = userList.get(0);
		DecimalFormat df = new DecimalFormat("#.######");
		df.setRoundingMode(RoundingMode.HALF_UP);

		String expected = "{\"" + firstUser.getUserId().toString() + "\":{\"longitude\":" + df.format(firstUser.getLastVisitedLocation().location.longitude) + ",\"latitude\":" + df.format(firstUser.getLastVisitedLocation().location.latitude) + "}";
		List<Map<String, Location>> resultList = tourGuideService.getAllCurrentLocations();

		Assert.assertEquals(userList.size(), resultList.size());
		Assert.assertTrue(JsonStream.serialize(resultList).contains(expected));
	}

	@Test
	public void setUserPreferences(){
		List<User> userList = tourGuideService.getAllUsers();
		User firstUser = userList.get(0);

		UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
		userPreferencesDTO.setNumberOfChildren(3);

		Assert.assertEquals(0, firstUser.getUserPreferences().getNumberOfChildren());

		tourGuideService.setUserPreferences(firstUser.getUserName(), userPreferencesDTO);

		Assert.assertEquals(3, firstUser.getUserPreferences().getNumberOfChildren());
	}

	@Test
	public void setUserPreferencesShouldReturnNull(){
		UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
		userPreferencesDTO.setNumberOfChildren(3);

		Assert.assertNull(tourGuideService.setUserPreferences("toto", userPreferencesDTO));
	}
}
