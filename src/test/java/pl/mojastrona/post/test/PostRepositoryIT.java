package pl.mojastrona.post.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.mojastrona.post.*;
import pl.mojastrona.post.test.helper.PostCreator;
import pl.mojastrona.post.test.helper.UserCreator;
import pl.mojastrona.user.User;
import pl.mojastrona.util.BaseIT;
import pl.mojastrona.util.BaseRepositoryIT;
import pl.mojastrona.util.BaseServiceIT;

import java.time.LocalDateTime;


class PostRepositoryIT extends BaseIT {

    @Autowired
    private PostRepository underTest;

    @Autowired
    private PostCreator postCreator;

    @Test
    void givenPosts_whenGetFind_thenCorrectResponse() throws Exception {
        //given

        User user = userCreator.createUser();
        String textContaining = "text";
        int page = 1;
        int size = 3;

        Post publishedAndActive1 = postCreator.createPost(user);
        Post publishedAndActive2 = postCreator.createPost(user, post -> post.setPublicationDate(null));
        Post publishedAndActive3 = postCreator.createPost(user);

        //not matching by deleted
        postCreator.createPost(user, post -> post.setStatus(PostStatus.DELETED));

        Post publishedAndActive4 = postCreator.createPost(user);

        // not matching by text
        postCreator.createPost(user, post -> post.setText("nie pasuje"));

        Post publishedAndActive5 = postCreator.createPost(user);

        //not matching not published
        Post notPublished = postCreator.createPost(user, post -> post.setPublicationDate(LocalDateTime.now().plusDays(1)));

        Post publishedAndActive6 = postCreator.createPost(user);



        Page<Post> resultPage = underTest.findActiveAndPublished(
                textContaining,
                LocalDateTime.now().plusSeconds(10),
                PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDateTime"))));

        //then
        Assertions.assertThat(resultPage).isNotNull();
        Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(6);
        Assertions.assertThat(resultPage.getContent())
                .hasSize(3)
                .extracting(Post::getId)
                .containsExactly(
                        publishedAndActive4.getId(),
                        publishedAndActive5.getId(),
                        publishedAndActive6.getId());

    }





}