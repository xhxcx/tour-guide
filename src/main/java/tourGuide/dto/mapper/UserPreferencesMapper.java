package tourGuide.dto.mapper;

import org.javamoney.moneta.Money;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.user.UserPreferences;

public class UserPreferencesMapper {
    public static UserPreferences map(UserPreferencesDTO userPreferencesDTO) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(userPreferencesDTO.getAttractionProximity());
        userPreferences.setHighPricePoint(Money.of(userPreferencesDTO.getHighPricePoint(),userPreferencesDTO.getCurrency()));
        userPreferences.setLowerPricePoint(Money.of(userPreferencesDTO.getLowerPricePoint(),userPreferencesDTO.getCurrency()));
        userPreferences.setNumberOfAdults(userPreferencesDTO.getNumberOfAdults());
        userPreferences.setNumberOfChildren(userPreferencesDTO.getNumberOfChildren());
        userPreferences.setTicketQuantity(userPreferencesDTO.getTicketQuantity());
        userPreferences.setTripDuration(userPreferencesDTO.getTripDuration());

        return userPreferences;
    }
}
