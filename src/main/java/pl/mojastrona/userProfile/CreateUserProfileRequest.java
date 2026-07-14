package pl.mojastrona.userProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class CreateUserProfileRequest {

    @NotBlank
    @Size(max = 100)
    String name;

    @NotBlank
    @Size(max = 100)
    String surname;

    @NotBlank
    @Size(max = 100)
    @Pattern(
            regexp = "^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$",
            message = "Podaj poprawny adres email np.example@mail.com"
    )
    String email;

    @NotBlank
    @Size(max = 20)
    String phone;

}

