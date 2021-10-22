package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.dto.ClosestAttractionDTO;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.dto.mapper.UserPreferencesMapper;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.ProviderTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.proxy.GpsUtilProxy;
import tourGuide.proxy.TripPricerProxy;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class TourGuideService {
	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);

	private final GpsUtilProxy gpsUtil;
	private final TripPricerProxy tripPricerProxy;

	private final RewardsService rewardsService;
	public final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	public final Tracker tracker = new Tracker(this);
	boolean testMode = true;
	
	@Autowired
	public TourGuideService(RewardsService rewardsService, GpsUtilProxy gpsUtil, TripPricerProxy tripPricerProxy) {
		Locale.setDefault(Locale.US);
		this.rewardsService = rewardsService;
		if(testMode) {
			logger.info("TestMode enabled");
			init();
		}
		this.gpsUtil = gpsUtil;
		this.tripPricerProxy = tripPricerProxy;
	}

	public void init(){
		logger.debug("Initializing users");
		initializeInternalUsers();
		logger.debug("Finished initializing users");
	}
	public void launchTracker(){
		addShutDownHook();
		scheduledExecutor.scheduleAtFixedRate(tracker::startTracking, 0, TRACKING_POLLING_INTERVAL, TimeUnit.SECONDS);
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocationTourGuide getUserLocation(User user) {
		return (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public UserPreferencesDTO setUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO) {
		User user = getUser(userName);
		if (user != null) {
			user.setUserPreferences(UserPreferencesMapper.map(userPreferencesDTO));
			return userPreferencesDTO;
		}
		return null;
	}
	
	public List<ProviderTourGuide> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<ProviderTourGuide> providers = tripPricerProxy.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints).getBody();
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocationTourGuide trackUserLocation(User user) {
		VisitedLocationTourGuide visitedLocation = gpsUtil.getUserLocation(user.getUserId().toString()).getBody();
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<ClosestAttractionDTO> getNearByAttractions(VisitedLocationTourGuide visitedLocation) {

		List<ClosestAttractionDTO> closestAttractionDTOList = new ArrayList<>();

		Objects.requireNonNull(gpsUtil.getAttractions().getBody()).forEach(attraction -> {
			double distance = rewardsService.getDistance(attraction, visitedLocation.location);
			closestAttractionDTOList.add(new ClosestAttractionDTO(attraction.attractionName, new LocationTourGuide(attraction.latitude, attraction.longitude), visitedLocation.location,
					distance, rewardsService.getRewardPoints(attraction, visitedLocation.userId)));
		});

		return closestAttractionDTOList.stream()
				.sorted(Comparator.comparingDouble(ClosestAttractionDTO::getDistance))
				.limit(5)
				.collect(Collectors.toList());
	}

	public List<Map<String, LocationTourGuide>> getAllCurrentLocations() {
		List<User> userList = getAllUsers();
		List<Map<String, LocationTourGuide>> currentLocationDTOList = new CopyOnWriteArrayList<>();
		userList.forEach(user -> {
			Map<String, LocationTourGuide> currentUserPosition = new HashMap<>();
			currentUserPosition.put(user.getUserId().toString(), getUserLocation(user).location);
			currentLocationDTOList.add(currentUserPosition);
		});
		return currentLocationDTOList;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      }
		    });
		Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutor::shutdownNow));
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocationTourGuide(user.getUserId(), new LocationTourGuide(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
