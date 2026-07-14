package pl.mojastrona.userProfile;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserService;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final LoggedUserProvider loggedUserProvider;

    @Transactional
    public void createOrUpdate(CreateUserProfileRequest createUserProfileRequest){

        User detachedUser = loggedUserProvider.provideLoggedUser();

        User managedUser = userService.findById(detachedUser.getId());

        UserProfile userProfile = userProfileRepository.findById(managedUser.getId())
                .orElseGet(() -> UserProfile.builder() // .orElseGet wywoła się tylko gdy baza danych zwróci pusty wynik - StrzeleckiRR
                        .user(managedUser)
                        .build());

        userProfile.setName(createUserProfileRequest.getName());
        userProfile.setSurname(createUserProfileRequest.getSurname());
        userProfile.setEmail(createUserProfileRequest.getEmail());
        userProfile.setPhone(createUserProfileRequest.getPhone());

        userProfileRepository.save(userProfile);
    }
}
