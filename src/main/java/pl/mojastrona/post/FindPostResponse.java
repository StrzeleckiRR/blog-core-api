package pl.mojastrona.post;

import java.time.LocalDateTime;

public record FindPostResponse(Long id, String text, LocalDateTime createdDateTime, String author) {

    public static FindPostResponse from(Post post) {
        return new FindPostResponse(
                post.getId(),
                post.getText(),
                post.getCreatedDateTime(),
                post.getAuthor()
        );
    }
}
