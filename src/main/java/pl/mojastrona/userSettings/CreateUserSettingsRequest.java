package pl.mojastrona.userSettings;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CreateUserSettingsRequest {

    @NotNull
    Long userId;

    @NotNull
    private Boolean showPanel1;

    @NotNull
    private Boolean darkMode;
}
