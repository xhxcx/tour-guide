package tourGuide.service;

import com.jsoniter.output.JsonStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tourGuide.dto.ClosestAttractionDTO;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.ProviderTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.TripPricerProxy;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;

public class TourGuideServiceTests {

    private final RewardsService rewardsServiceMock = Mockito.mock(RewardsService.class);
    private final GpsUtilProxy gpsUtilProxyMock = Mockito.mock(GpsUtilProxy.class);
    private final TripPricerProxy tripPricerProxyMock = Mockito.mock(TripPricerProxy.class);
    private final TourGuideService tourGuideService = new TourGuideService(rewardsServiceMock, gpsUtilProxyMock, tripPricerProxyMock);

    @Test
    public void getUserRewardsTest(){
        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");
        AttractionTourGuide attractionTourGuide = new AttractionTourGuide("attractionName", "cityName", "state", 10, 10);
        UserReward userReward = new UserReward(new VisitedLocationTourGuide(),attractionTourGuide);
        userReward.setRewardPoints(10);
        user.addUserReward(userReward);

        Assert.assertEquals(1, tourGuideService.getUserRewards(user).size());
        Assert.assertEquals(10, tourGuideService.getUserRewards(user).get(0).getRewardPoints());
    }

    @Test
    public void getUserLocationWithAlreadyKnownVisitedLocationTest(){
        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");
        VisitedLocationTourGuide visitedLocation = new VisitedLocationTourGuide();
        visitedLocation.setUserId(user.getUserId());
        visitedLocation.setLocation(new LocationTourGuide(10,10));
        user.addToVisitedLocations(visitedLocation);

        Assert.assertEquals(visitedLocation, tourGuideService.getUserLocation(user));
    }

    @Test
    public void getUserLocationWithNoVisitedLocationTest(){
        VisitedLocationTourGuide newVisitedLocation = new VisitedLocationTourGuide();
        newVisitedLocation.setUserId(UUID.fromString("00000000-0000-0001-0000-000000000002"));
        newVisitedLocation.setLocation(new LocationTourGuide(10,10));

        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");

        Mockito.when(gpsUtilProxyMock.getUserLocation(user.getUserId().toString())).thenReturn(new ResponseEntity<>(newVisitedLocation, HttpStatus.OK));

        Assert.assertEquals(newVisitedLocation, tourGuideService.getUserLocation(user));
        Mockito.verify(rewardsServiceMock, Mockito.times(1)).calculateRewards(user);
    }

    @Test
    public void setUserPreferences(){
        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");
        tourGuideService.addUser(user);
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setNumberOfChildren(3);

        Assert.assertEquals(0, user.getUserPreferences().getNumberOfChildren());

        tourGuideService.setUserPreferences(user.getUserName(), userPreferencesDTO);

        Assert.assertEquals(3, user.getUserPreferences().getNumberOfChildren());
    }

    @Test
    public void setUserPreferencesShouldReturnNull(){
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setNumberOfChildren(3);

        Assert.assertNull(tourGuideService.setUserPreferences("toto", userPreferencesDTO));
    }

    @Test
    public void getTripDealsTest(){
        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");

        List<ProviderTourGuide> providerTourGuideList = Collections.singletonList(new ProviderTourGuide(UUID.randomUUID(), "name", 100));
        Mockito.when(tripPricerProxyMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(new ResponseEntity<>(providerTourGuideList, HttpStatus.OK));

        List<ProviderTourGuide> resultList = tourGuideService.getTripDeals(user);

        Assert.assertTrue(resultList.containsAll(providerTourGuideList));
        Assert.assertTrue(user.getTripDeals().containsAll(resultList));
    }

    @Test
    public void getNearByAttractionsTest(){
        AttractionTourGuide existingAttraction = new AttractionTourGuide("attractionName", "cityName", "state", 10, 10);
        List<AttractionTourGuide> existingAttractionList = Collections.singletonList(existingAttraction);

        VisitedLocationTourGuide visitedLocation = new VisitedLocationTourGuide();
        visitedLocation.setUserId(UUID.fromString("00000000-0000-0001-0000-000000000002"));
        visitedLocation.setLocation(new LocationTourGuide(10,10));

        Mockito.when(gpsUtilProxyMock.getAttractions()).thenReturn(new ResponseEntity<>(existingAttractionList, HttpStatus.OK));
        Mockito.when(rewardsServiceMock.getDistance(existingAttraction, visitedLocation.getLocation())).thenReturn(1.0);
        Mockito.when(rewardsServiceMock.getRewardPoints(existingAttraction, visitedLocation.getUserId())).thenReturn(100);

        ClosestAttractionDTO closestAttractionDTO = new ClosestAttractionDTO("attractionName", new LocationTourGuide(10,10), visitedLocation.location, 1.0, 100);
        List<ClosestAttractionDTO> expectedResultList = Collections.singletonList(closestAttractionDTO);

        List<ClosestAttractionDTO> resultList = tourGuideService.getNearByAttractions(visitedLocation);

        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedResultList.get(0).getAttractionName(), resultList.get(0).getAttractionName());

    }

    // TODO bof le TU
    @Test
    public void getAllCurrentLocations() {
        List<User> userList = tourGuideService.getAllUsers();
        User firstUser = userList.get(0);
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String expected = "{\"" + firstUser.getUserId().toString() + "\":{\"longitude\":" + df.format(firstUser.getLastVisitedLocation().location.longitude) + ",\"latitude\":" + df.format(firstUser.getLastVisitedLocation().location.latitude) + "}";
        List<Map<String, LocationTourGuide>> resultList = tourGuideService.getAllCurrentLocations();

        Assert.assertEquals(userList.size(), resultList.size());
        Assert.assertTrue(JsonStream.serialize(resultList).contains(expected));
    }
}
