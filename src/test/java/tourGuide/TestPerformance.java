package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPerformance {
	
	 /* A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Autowired
	private GpsUtilProxy gpsUtilProxy;

	@Autowired
	private RewardsService rewardsService;

	@Autowired
	private TourGuideService tourGuideService;

	@Autowired
	private Tracker tracker;

	@Test
	public void highVolumeTrackLocation() {
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		tourGuideService.init();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		tracker.startTracking();
		TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime());

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + stopWatch.getTime() + " milliseconds.");
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards() {
		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		tourGuideService.init();
		tracker.startTracking();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

	    AttractionTourGuide attraction = Objects.requireNonNull(gpsUtilProxy.getAttractions().getBody()).get(0);
		List<User> allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocationTourGuide(u.getUserId(), attraction, new Date())));

	    allUsers.forEach(u -> rewardsService.calculateRewards(u));

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
