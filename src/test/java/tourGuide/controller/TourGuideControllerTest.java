package tourGuide.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TourGuideControllerTest {

    @MockBean
    private TourGuideService tourGuideService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void setUserPreferencesShouldReturn200WhenPreferencesAreSetOnExistingUser() throws Exception{
        User user = new User(new UUID(1,2), "name", "123564", "email");

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setNumberOfChildren(3);

        Mockito.when(tourGuideService.setUserPreferences(anyString(), any(UserPreferencesDTO.class))).thenReturn(userPreferencesDTO);

        mockMvc.perform(post("/userPreferences")
                .param("userName", "name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userPreferencesDTO)))
                .andExpect(mvcResult -> {
                    Assert.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
                });
    }

    @Test
    public void setUserPreferencesShouldReturn404UserNotFound() throws Exception{

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setNumberOfChildren(3);

        Mockito.when(tourGuideService.setUserPreferences(anyString(), any(UserPreferencesDTO.class))).thenReturn(null);

        mockMvc.perform(post("/userPreferences")
                .param("userName", "name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userPreferencesDTO)))
                .andExpect(mvcResult -> {
                    Assert.assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
                });
    }

}
