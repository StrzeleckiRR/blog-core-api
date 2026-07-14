package pl.mojastrona.userProfile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public void createOrUpdate(@Valid @RequestBody CreateUserProfileRequest createUserProfileRequest) {
        userProfileService.createOrUpdate(createUserProfileRequest);
    }
}
