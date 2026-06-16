package pl.mojastrona.post;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


public class CreatePostRequest {

    @NotBlank
    @Size(max = 5000)
    private final String text;

    @NotNull
    private final PostScope scope;

    @FutureOrPresent
    private final LocalDateTime publicationDate;

    public CreatePostRequest(String text, PostScope scope, LocalDateTime publicationDate) {
        this.text = text;
        this.scope = scope;
        this.publicationDate = publicationDate;
    }

    public String getText() {
        return text;
    }

    public PostScope getScope() {
        return scope;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }
}
