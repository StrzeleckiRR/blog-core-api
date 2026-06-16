package pl.mojastrona.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class CreateAddressRequest {

    @NotNull
    Long userId;

    @NotBlank
    @Size(max = 100)
    String street;

    @NotBlank
    @Size(max = 100)
    String city;
}
