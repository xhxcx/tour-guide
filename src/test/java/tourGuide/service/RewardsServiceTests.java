package tourGuide.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.RewardCentralProxy;
import tourGuide.user.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;

public class RewardsServiceTests {
    private final GpsUtilProxy gpsUtilMock = Mockito.mock(GpsUtilProxy.class);
    private final RewardCentralProxy rewardCentralMock = Mockito.mock(RewardCentralProxy.class);

    private final RewardsService rewardsService = new RewardsService(gpsUtilMock, rewardCentralMock);

    private AttractionTourGuide attractionTourGuide;

    @Before
    public void setUp(){
        attractionTourGuide = new AttractionTourGuide("attractionName", "cityName", "state", 10, 10);
    }

    @Test
    public void calculateRewardsTest(){
        List<AttractionTourGuide> attractionTourGuideList = Collections.singletonList(attractionTourGuide);

        Mockito.when(gpsUtilMock.getAttractions()).thenReturn(new ResponseEntity<>(attractionTourGuideList, HttpStatus.OK));

        User user = new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "userName", "phone", "email");
        VisitedLocationTourGuide visitedLocation = new VisitedLocationTourGuide();
        visitedLocation.setUserId(UUID.fromString("00000000-0000-0001-0000-000000000002"));
        visitedLocation.setLocation(new LocationTourGuide(10,10));
        user.addToVisitedLocations(visitedLocation);

        Mockito.when(rewardCentralMock.getAttractionRewardPoints(anyString(), anyString())).thenReturn(new ResponseEntity<>(10, HttpStatus.OK));

        rewardsService.calculateRewards(user);

        Assert.assertFalse(user.getUserRewards().isEmpty());
        Assert.assertEquals(10, user.getUserRewards().get(0).getRewardPoints());
    }

    @Test
    public void isWithinAttractionProximityTrueTest(){
        LocationTourGuide locationTourGuide = new LocationTourGuide(10,10);

        Assert.assertTrue(rewardsService.isWithinAttractionProximity(attractionTourGuide, locationTourGuide));
    }

    @Test
    public void isWithinAttractionProximityFalseTest(){
        LocationTourGuide locationTourGuide = new LocationTourGuide(100000,1000000);

        Assert.assertFalse(rewardsService.isWithinAttractionProximity(attractionTourGuide, locationTourGuide));
    }

    @Test
    public void getRewardPointsTest(){
        Mockito.when(rewardCentralMock.getAttractionRewardPoints(anyString(), anyString())).thenReturn(new ResponseEntity<>(10, HttpStatus.OK));
        Assert.assertEquals(10, rewardsService.getRewardPoints(attractionTourGuide, UUID.randomUUID()));
    }
}
