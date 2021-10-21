package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.dto.ClosestAttractionDTO;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.ProviderTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestTourGuideService {

    @Autowired
    private TourGuideService tourGuideService;

	private final User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

	@Before
	public void setUp() {
		InternalTestHelper.setInternalUserNumber(1);
	}

	@Test
	public void getUserLocation() {
		VisitedLocationTourGuide visitedLocation = tourGuideService.trackUserLocation(user);
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
		
		assertTrue(allUsers.stream().anyMatch(u -> u.getUserName().equalsIgnoreCase(user.getUserName())));
		assertTrue(allUsers.stream().anyMatch(u -> u.getUserName().equalsIgnoreCase(user2.getUserName())));
	}
	
	@Test
	public void trackUser() {
		VisitedLocationTourGuide visitedLocation = tourGuideService.trackUserLocation(user);
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		VisitedLocationTourGuide visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<ClosestAttractionDTO> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {

		List<ProviderTourGuide> providers = tourGuideService.getTripDeals(user);
		
		assertEquals(5, providers.size());
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
