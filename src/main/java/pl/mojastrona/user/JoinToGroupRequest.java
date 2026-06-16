package pl.mojastrona.user;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class JoinToGroupRequest {

    @NotNull
    Long userId;

    @NotNull
    Long groupId;
}
