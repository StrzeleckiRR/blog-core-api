package pl.mojastrona.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class CreateCommentRequest {

    @NotBlank
    @Size(max = 5000)
    String text;

    @NotNull
    Long postId;


}
