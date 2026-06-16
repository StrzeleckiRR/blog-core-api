package pl.mojastrona.post;

import lombok.Value;
import pl.mojastrona.comment.Comment;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Value
class ReadPostResponse {

    Long id;

    Integer version;

    String text;

    String author;

    LocalDateTime createdDateTime;

    LocalDateTime publicationDate;

    PostScope scope;

    PostStatus status;

    List<CommentResponse> comments;


    public static ReadPostResponse from(Post post) {
        return new ReadPostResponse(
                post.getId(),
                post.getVersion(),
                post.getText(),
                post.getAuthor(),
                post.getCreatedDateTime(),
                post.getPublicationDate(),
                post.getScope(),
                post.getStatus(),
                post.getComments().stream()
                        .map(CommentResponse::from)
                        .sorted(Comparator.comparing(CommentResponse::getCreatedDateTime).reversed())
                        .collect(Collectors.toList()));

    }


    @Value
    public static class CommentResponse {

        Long id;

        String text;

        LocalDateTime createdDateTime;

        String author;


        public static CommentResponse from(Comment comment) {
            return new CommentResponse(
                    comment.getId(),
                    comment.getText(),
                    comment.getCreatedDateTime(),
                    comment.getAuthor()
            );

        }
    }
}
