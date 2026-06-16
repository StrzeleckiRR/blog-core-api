package pl.mojastrona.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    @Size(max = 100)
    String login;

    @NotBlank
    @Size(max = 30)
    String password;
}
