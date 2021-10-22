package tourGuide.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;

import java.util.List;

@FeignClient(name = "gpsUtil-service", url = "${gpsutil.service.url}:8081")
public interface GpsUtilProxy {
    @RequestMapping("/getAttraction")
    ResponseEntity<List<AttractionTourGuide>> getAttractions();

    @RequestMapping("/getUserLocation")
    ResponseEntity<VisitedLocationTourGuide> getUserLocation (@RequestParam("userId") String userId);
}
