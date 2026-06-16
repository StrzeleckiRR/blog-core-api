package pl.mojastrona.accountant;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AttachClientRequest {

    @NotNull
    Long accountantId;

    @NotNull
    Long clientId;
}
