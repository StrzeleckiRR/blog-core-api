package pl.mojastrona.post.test.helper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.comment.Comment;
import pl.mojastrona.post.Post;
import pl.mojastrona.post.PostScope;
import pl.mojastrona.post.PostStatus;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class CommentCreator {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Comment createComment(Post post) {

        Comment comment = Comment.builder()
                .author("Marcin Strzelecki")
                .text("text")
                .post(post)
                .user(post.getUser())
                .build();

        entityManager.persist(comment);
        return comment;
    }

    @Transactional
    public Comment createComment(Post post, int number) {

        Comment comment = Comment.builder()
                .author("Marcin Strzelecki")
                .text("text" + number)
                .post(post)
                .user(post.getUser())
                .build();

        entityManager.persist(comment);
        return comment;
    }
}
