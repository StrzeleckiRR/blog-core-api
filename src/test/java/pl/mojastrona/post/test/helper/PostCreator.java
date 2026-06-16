package pl.mojastrona.post.test.helper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.post.Post;
import pl.mojastrona.post.PostScope;
import pl.mojastrona.post.PostStatus;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

@Component
public class PostCreator  {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CommentCreator commentCreator;

    @Transactional
    public Post createPost(User user) {

        LocalDateTime expectedPublicationDatePost = LocalDateTime.now().plus(1000, ChronoUnit.MILLIS).truncatedTo(ChronoUnit.SECONDS);

        Post post = Post.builder()
                .author("Marcin Strzelecki")
                .text("domyslny text")
                .scope(PostScope.PUBLIC)
                .publicationDate(expectedPublicationDatePost)
                .status(PostStatus.ACTIVE)
                .user(user)
                .build();

        entityManager.persist(post);
        return post;
    }

    @Transactional
    public Post createPost(User user, Consumer<Post> modifier) {

        Post post = createPost(user);

        modifier.accept(post);
        entityManager.persist(post);
        return post;
    }

    @Transactional
    public Post createPostWithComment(User user) {

        Post post = createPost(user);
        commentCreator.createComment(post);
        return post;
    }

    @Transactional
    public Post createPostWithComments(User user,int commentsNumber) {

        Post post = createPost(user);
        for(int i = 0; i < commentsNumber; i++){
            commentCreator.createComment(post, i);
        }
        return post;
    }
}
