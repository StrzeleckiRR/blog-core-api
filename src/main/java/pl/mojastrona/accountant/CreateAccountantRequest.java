package pl.mojastrona.accountant;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor_ = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))// wymagane gdy mamy jedno pole
public class CreateAccountantRequest {

    @NotBlank
    @Size(max = 100)
    String name;
}
