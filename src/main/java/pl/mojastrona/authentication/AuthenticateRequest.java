package pl.mojastrona.authentication;

import jakarta.validation.constraints.Size;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;

@Value
public class AuthenticateRequest {

        @NotBlank
        @Size(max = 100)
        String login;

        @NotBlank
        @Size(max = 30)
        String password;

}
