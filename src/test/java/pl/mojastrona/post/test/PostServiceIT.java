package pl.mojastrona.post.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;
import pl.mojastrona.post.*;
import pl.mojastrona.post.test.helper.PostCreator;
import pl.mojastrona.post.test.helper.UserCreator;
import pl.mojastrona.user.CreateUserRequest;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserRole;
import pl.mojastrona.util.BaseIT;
import pl.mojastrona.util.BaseServiceIT;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PostServiceIT extends BaseIT {

    @Autowired
    private PostService underTest;

    @Autowired
    private PostCreator postCreator;

    @Test
    void givenPosts_whenGetFind_thenCorrectResponse() throws Exception {
        //given
        User user = userCreator.createUser();
        String textContaining = "ex";
        int page = 0;
        int size = 3;


        Post publishedAndActive1 = postCreator.createPost(user);
        Post publishedAndActive2 = postCreator.createPost(user,post -> post.setPublicationDate(null));
        Post publishedAndActive3 = postCreator.createPost(user);

        //not matching by deleted
        postCreator.createPost(user,post -> post.setStatus(PostStatus.DELETED));

        Post publishedAndActive4 = postCreator.createPost(user);

        // not matching by text
        postCreator.createPost(user, post -> post.setText("nie pasuje"));

        Post publishedAndActive5 = postCreator.createPost(user);

        //not matching not published
        Post notPublished = postCreator.createPost(user, post -> post.setPublicationDate(LocalDateTime.now().plusDays(1)));

        Post publishedAndActive6 = postCreator.createPost(user);

        Thread.sleep(1100);


        Page<FindPostResponse> resultPage = underTest.find(textContaining, page, size);

        //then
        Assertions.assertThat(resultPage).isNotNull();
        Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(6);
        Assertions.assertThat(resultPage.getContent())
                .hasSize(3)
                .extracting(FindPostResponse::id)
                .containsExactly(
                        publishedAndActive6.getId(),
                        publishedAndActive5.getId(),
                        publishedAndActive4.getId());

    }





}