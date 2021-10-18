package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService();
	}
}
