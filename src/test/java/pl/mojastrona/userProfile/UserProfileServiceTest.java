package pl.mojastrona.userProfile;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserService;
import pl.mojastrona.util.BaseIT;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileServiceTest extends BaseIT {

    public static final String PATH_POST_URL = "/api/user/profile";

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserService userService;

    @Mock
    private LoggedUserProvider loggedUserProvider;

    @Captor
    ArgumentCaptor<UserProfile> argumentCaptor;

    @InjectMocks
    private UserProfileService underTest;

    private final String expectedName = "Marcin";
    private final String expectedSurname = "Strzelecki";
    private final String expectedEmail = "example@gmail.com";
    private final String expectedPhone = "000111222";


    @Test
    void givenInCorrectRequest_whenCreateOrUpdate_then401() throws Exception {
        //given


        CreateUserProfileRequest createUserProfileRequest = new CreateUserProfileRequest(expectedName, expectedSurname, expectedEmail,expectedPhone);


        ResultActions resultActions = performPost(PATH_POST_URL, createUserProfileRequest);

        //then
        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    void givenInvalidEmail_whenCreateOrUpdate_then400BadRequest() throws Exception {

        CreateUserProfileRequest invalidRequest = new CreateUserProfileRequest(expectedName,expectedSurname,"incorrectEmail", "458659123");


        ResultActions resultActions = mockMvc.perform(post(PATH_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));


        resultActions.andExpect(status().isBadRequest()); // Zwraca 400
    }

    @Test
    void givenCorrectRequest_whenCreateOrUpdate_thenCreateUserProfile() throws Exception{


        CreateUserProfileRequest createUserProfileRequest = new CreateUserProfileRequest(expectedName, expectedSurname, expectedEmail, expectedPhone);

        long expectedIdProfileUser = 67L;
        User newUser = new User();
        newUser.setId(expectedIdProfileUser);

        Mockito.when(loggedUserProvider.provideLoggedUser()).thenReturn(newUser);
        Mockito.when(userService.findById(expectedIdProfileUser)).thenReturn(newUser);
        Mockito.when(userProfileRepository.findById(expectedIdProfileUser)).thenReturn(Optional.empty());

        //Act(wykonanie)
        underTest.createOrUpdate(createUserProfileRequest);

        //Assert(weryfikacja)

        Mockito.verify(userProfileRepository).save(argumentCaptor.capture());
        // verify() - "Upewnij się, że metoda save została wywołana"
        // argumentCaptor.capture() - "Złap argument, który został przekazany do save"

        UserProfile userProfile = argumentCaptor.getValue();
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getUser().getId()).isEqualTo(expectedIdProfileUser);
        assertThat(userProfile.getName()).isEqualTo(expectedName);
        assertThat(userProfile.getSurname()).isEqualTo(expectedSurname);
        assertThat(userProfile.getEmail()).isEqualTo(expectedEmail);
        assertThat(userProfile.getPhone()).isEqualTo(expectedPhone);

    }
}