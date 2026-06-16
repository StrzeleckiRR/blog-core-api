package pl.mojastrona.userSettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserService;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserService userService;


    @Transactional
    public void createOrUpdate(CreateUserSettingsRequest createUserSettingsRequest){

        User user = userService.findById(createUserSettingsRequest.getUserId());

       UserSettings userSettings= userSettingsRepository.findById(createUserSettingsRequest.getUserId())
                .orElse(UserSettings.builder()
                .user(user)
                .build());

        userSettings.setShowPanel1(createUserSettingsRequest.getShowPanel1());
        userSettings.setDarkMode(createUserSettingsRequest.getDarkMode());

        userSettingsRepository.save(userSettings);
    }
}
