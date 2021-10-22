package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rewardCentral-service", url = "${rewardcentral.service.url}:8082")
public interface RewardCentralProxy {

    @GetMapping("/getRewardPoints")
    ResponseEntity<Integer> getAttractionRewardPoints(@RequestParam("attractionId") String attractionId, @RequestParam("userId") String userId);
}
