package pl.mojastrona.comment;

import lombok.Value;
import pl.mojastrona.post.Post;
import pl.mojastrona.post.PostScope;
import pl.mojastrona.post.PostStatus;

import java.time.LocalDateTime;

@Value
public class ReadCommentResponse {

    Long id;

    String text;

    LocalDateTime createdDateTime;

    String author;

    PostResponse post;


    public static ReadCommentResponse from(Comment comment) {
        return new ReadCommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getCreatedDateTime(),
                comment.getAuthor(),
                PostResponse.from(comment.getPost())
        );

    }

    @Value
    static class PostResponse {

        Long id;

        Integer version;

        String text;

        String author;

        LocalDateTime publicationDate;

        LocalDateTime createdDateTime;

        PostScope scope;

        PostStatus status;


        public static PostResponse from(Post post) {
            return new PostResponse(
                    post.getId(),
                    post.getVersion(),
                    post.getText(),
                    post.getAuthor(),
                    post.getPublicationDate(),
                    post.getCreatedDateTime(),
                    post.getScope(),
                    post.getStatus());
        }
    }
}




