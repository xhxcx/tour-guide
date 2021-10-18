package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.ProviderTourGuide;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tripPricer-service", url = "localhost:8083")
public interface TripPricerProxy {

    @RequestMapping("/getPrice")
    ResponseEntity<List<ProviderTourGuide>> getPrice(@RequestParam("apiKey") String apiKey, @RequestParam("attractionId") UUID attractionId,
                                                     @RequestParam("adults") int adults, @RequestParam("children") int children,
                                                     @RequestParam("nightsStay") int nightsStay, @RequestParam("rewardsPoints") int rewardsPoints);

    @RequestMapping("/getProviderName")
    ResponseEntity<String> getProviderName(@RequestParam("apiKey") String apiKey, @RequestParam("adults") int adults);
}
