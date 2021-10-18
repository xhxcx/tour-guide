package tourGuide.tracker;

import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tourGuide.service.TourGuideService;
import tourGuide.user.User;

public class Tracker {
	private final Logger logger = LoggerFactory.getLogger(Tracker.class);
	private final TourGuideService tourGuideService;

	private boolean stop = false;
	public ExecutorService executorService;

	public Tracker(TourGuideService tourGuideService) {
		this.tourGuideService = tourGuideService;
	}
	
	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	public void startTracking() {
		StopWatch stopWatch = new StopWatch();
		executorService = Executors.newFixedThreadPool(200);

		if(Thread.currentThread().isInterrupted() || stop) {
			logger.debug("Tracker stopping");
			return;
		}

		List<User> users = tourGuideService.getAllUsers();
		logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
		stopWatch.start();
		users.forEach(user ->
				executorService.execute(() ->
						tourGuideService.trackUserLocation(user))
		);
		executorService.shutdown();
		try {
			boolean hasFinished = executorService.awaitTermination(300, TimeUnit.SECONDS);
			if (!hasFinished) {
				logger.debug("fail to finish before 200 sec");
				tourGuideService.scheduledExecutor.shutdownNow();
			}
			else {
				stopWatch.stop();
				logger.debug("finished in " + TimeUnit.SECONDS.toSeconds(stopWatch.getTime()));
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
