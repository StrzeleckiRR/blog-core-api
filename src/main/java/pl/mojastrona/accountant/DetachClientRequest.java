package pl.mojastrona.accountant;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DetachClientRequest {

    @NotNull
    Long accountantId;

    @NotNull
    Long clientId;
}
