package pl.mojastrona.post;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import pl.mojastrona.comment.Comment;
import pl.mojastrona.post.test.helper.CommentCreator;
import pl.mojastrona.post.test.helper.PostCreator;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserRole;
import pl.mojastrona.util.BaseIT;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class PostControllerIT extends BaseIT {

    public static final String PATH_POST_URL = "/api/posts";

    @Autowired
    private PostCreator postCreator;

    @Autowired
    private CommentCreator commentCreator;

    @Autowired
    private PostRepository postRepository;

    @Test
    void givenNotAuthenticated_whenCreate_then401() throws Exception {
        //given


        CreatePostRequest request = new CreatePostRequest(null, null, null);


        ResultActions resultActions = performPost(PATH_POST_URL, request);

        //then
        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void givenAuthenticatedUserNotAuthor_whenUpdate_then403() throws Exception {
        //given
        createUserAndAuthenticate();
        User author = userCreator.createUser("JosephEgypt", UserRole.USER);

        Post post = postCreator.createPostWithComment(author);


        String expectedNewText = "New text";
        PostScope expectedUpdatedScope = PostScope.PRIVATE;
        UpdatePostRequest request = new UpdatePostRequest(expectedNewText, expectedUpdatedScope, post.getVersion());

        Long id = post.getId();


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isForbidden());

        Post shouldntBeUpdatedPost = entityManager.find(Post.class, id);

        assertThat(shouldntBeUpdatedPost.getLastModifiedDateTime()).isEqualToIgnoringNanos(post.getCreatedDateTime());
        assertThat(shouldntBeUpdatedPost.getCreatedDateTime()).isEqualToIgnoringNanos(post.getCreatedDateTime());

        assertThat(shouldntBeUpdatedPost).extracting(
                Post::getVersion,
                Post::getText,
                Post::getPublicationDate,
                Post::getScope,
                Post::getStatus
        ).containsExactly(
                post.getVersion(),
                post.getText(),
                post.getPublicationDate(),
                post.getScope(),
                post.getStatus()
        );

    }

    @Test
    void givenWrongRequest_whenCreate_thenBadRequest() throws Exception {
        //given
        createUserAndAuthenticate();

        CreatePostRequest request = new CreatePostRequest(null, null, null);


        ResultActions resultActions = performPost(PATH_POST_URL, request);

        //then
        resultActions.andExpect(status().isBadRequest()) // Can you use MockMvcResultMatchers method static
                .andExpect(jsonPath("$.*", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.scope").value("must not be null"))
                .andExpect(jsonPath("$.text").value("must not be blank"))
        ;

    }

    @Test
    //@WithUserDetails(value = "Tester", userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenCorrectRequest_whenCreate_thenCreatePost() throws Exception {
        //given
        User user = createUserAndAuthenticate();
        String expectedText = "text";
        PostScope expectedPostScape = PostScope.PUBLIC;
        LocalDateTime expectedLocalDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        CreatePostRequest request = new CreatePostRequest(
                expectedText,
                expectedPostScape,
                expectedLocalDate
        );

        ResultActions resultActions = performPost(PATH_POST_URL, request);

        //then
        resultActions.andExpect(status().isOk());

        List<Post> postsList = entityManager.createQuery("select p from Post p left join fetch p.comments").getResultList();
        assertThat(postsList).hasSize(1);
        Post post = postsList.get(0);

        assertThat(post).extracting(
                Post::getId,
                Post::getCreatedDateTime,
                Post::getLastModifiedDateTime
        ).isNotNull();

        assertThat(post).extracting(
                Post::getVersion,
                Post::getText,
                Post::getAuthor,
                Post::getPublicationDate,
                Post::getScope,
                Post::getStatus
        ).containsExactly(
                0,
                expectedText,
                user.getLogin(),
                expectedLocalDate,
                expectedPostScape,
                PostStatus.ACTIVE
        );

        assertThat(post.getUser().getId()).isEqualTo(user.getId());
        assertThat(post.getComments()).isEmpty();

    }

    @Test
    void givenNotAuthenticated_whenUpdate_then401() throws Exception {
        //given
        UpdatePostRequest request = new UpdatePostRequest(null, null, null);

        Long id = 100L;


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void givenWrongRequest_whenUpdate_thenBadRequest() throws Exception {
        //given
        createUserAndAuthenticate();
        UpdatePostRequest request = new UpdatePostRequest(null, null, null);

        Long id = 100L;


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isBadRequest()) // Can you use MockMvcResultMatchers method static
                .andExpect(jsonPath("$.*", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.scope").value("must not be null"))
                .andExpect(jsonPath("$.text").value("must not be blank"))
                .andExpect(jsonPath("$.version").value("must not be null"))
        ;

    }

    @Test
    void givenNotExistingPost_whenUpdate_thenNotFound() throws Exception {
        //given
        createUserAndAuthenticate();
        UpdatePostRequest request = new UpdatePostRequest("text", PostScope.PUBLIC, 0);

        Long id = 100L;


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

    }



    @Test
    void givenCorrectRequest_whenUpdate_thenUpdatePost() throws Exception {
        //given
        User user = createUserAndAuthenticate();

        Post post = postCreator.createPostWithComment(user);


        String expectedNewText = "New text";
        PostScope expectedUpdatedScope = PostScope.PRIVATE;
        UpdatePostRequest request = new UpdatePostRequest(expectedNewText, expectedUpdatedScope, post.getVersion());

        Long id = post.getId();


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(is(emptyString())));

        Post updatedPost = entityManager.createQuery("select p from Post p left join fetch p.comments where p.id=:id", Post.class)
                .setParameter("id", id)
                .getSingleResult(); // pojedynczy wynik

        assertThat(updatedPost.getLastModifiedDateTime()).isAfter(post.getCreatedDateTime());
        assertThat(updatedPost.getCreatedDateTime()).isEqualToIgnoringNanos(post.getCreatedDateTime());

        assertThat(updatedPost).extracting(
                Post::getVersion,
                Post::getText,
                Post::getPublicationDate,
                Post::getScope,
                Post::getStatus
        ).containsExactly(
                post.getVersion() + 1,
                expectedNewText,
                post.getPublicationDate(),
                expectedUpdatedScope,
                post.getStatus()
        );

        assertThat(updatedPost.getComments()).hasSize(1);
        Comment comment = updatedPost.getComments().iterator().next();
        assertThat(comment.getLastModifiedDateTime()).isEqualToIgnoringNanos(comment.getCreatedDateTime());

    }

    @Test
    void givenWrongVersion_whenUpdate_thenConflict() throws Exception {
        //given

        User user = createUserAndAuthenticate();

        Post post = postCreator.createPost(user);

        int wrongVersion = post.getVersion() + 1;

        UpdatePostRequest request = new UpdatePostRequest("new text", PostScope.PUBLIC, wrongVersion);

        Long id = post.getId();


        ResultActions resultActions = performPut(PATH_POST_URL + "/{id}", id, request);

        //then
        resultActions.andExpect(status().isConflict())
                .andExpect(content().string(is(emptyString())));

        Post shouldntBeUpdatedPost = entityManager.find(Post.class, id);

        assertThat(shouldntBeUpdatedPost.getLastModifiedDateTime()).isEqualToIgnoringNanos(post.getCreatedDateTime());
        assertThat(shouldntBeUpdatedPost.getCreatedDateTime()).isEqualToIgnoringNanos(post.getCreatedDateTime());

        assertThat(shouldntBeUpdatedPost).extracting(
                Post::getVersion,
                Post::getText,
                Post::getPublicationDate,
                Post::getScope,
                Post::getStatus
        ).containsExactly(
                post.getVersion(),
                post.getText(),
                post.getPublicationDate(),
                post.getScope(),
                post.getStatus()
        );

    }

    @Test
    void givenNotExistingPost_whenRead_thenNotFound() throws Exception {
        //given
        Long id = 100L;


        ResultActions resultActions = performGet(PATH_POST_URL + "/{id}", id);

        //then
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().string(is(emptyString())));

    }

    @Test
    void givenExistingPost_whenRead_thenReturnResponse() throws Exception {
        //given
        User user = createUserAndAuthenticate();
        Post post = postCreator.createPost(user);
        Long postId = post.getId();

        Comment comment1 = commentCreator.createComment(post, 1);
        Comment comment2 = commentCreator.createComment(post, 2);
        Comment comment3 = commentCreator.createComment(post, 3);

        List<Comment> commentList = List.of(comment3, comment2, comment1);


        ResultActions resultActions = performGet(PATH_POST_URL + "/{id}", postId);

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.version").value(post.getVersion()))
                .andExpect(jsonPath("$.text").value(post.getText()))
                .andExpect(jsonPath("$.publicationDate").value(post.getPublicationDate().truncatedTo(ChronoUnit.MILLIS).toString()))
                .andExpect(jsonPath("$.scope").value(post.getScope().toString()))
                .andExpect(jsonPath("$.status").value(post.getStatus().toString()))
                //.andExpect(jsonPath("$.createdDateTime").value(post.getCreatedDateTime().truncatedTo(ChronoUnit.MICROS).toString()))

                .andExpect(jsonPath("$.comments[*]", hasSize(commentList.size())))
                .andExpect(jsonPath("$.comments[*].id").value(contains(comment3.getId().intValue(), comment2.getId().intValue(), comment1.getId().intValue()))

                );


        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        String postCreatedDateTimeStr = JsonPath.compile("$.createdDateTime").read(contentAsString);
        LocalDateTime postCreatedDateTime = LocalDateTime.parse(postCreatedDateTimeStr);
        assertThat(postCreatedDateTime).isEqualToIgnoringNanos(post.getCreatedDateTime());

        int i = 0;
        for (Comment comment : commentList) {

            resultActions.andExpect(jsonPath("$.comments["+ i +"].text").value(comment.getText()))
                    //.andExpect(jsonPath("$.comments["+ i +"].createdDateTime").value(comment.getCreatedDateTime().truncatedTo(ChronoUnit.MICROS)))
                    .andExpect(jsonPath("$.comments["+ i +"].author").value(comment.getAuthor()));

            LocalDateTime commentCreatedDateTime = parseDateTime(contentAsString, "$.comments[" + i + "].createdDateTime");
            assertThat(commentCreatedDateTime).isEqualToIgnoringNanos(comment.getCreatedDateTime());
            i++;
        }


    }

    @Test
    void givenNoPostInDb_whenGetFind_thenEmptyList() throws Exception {
        //given

        ResultActions resultActions = performGet(PATH_POST_URL,
                Map.of(
                        "q", "text",
                        "page", "0",
                        "size", "3"
                )
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(empty())))
                .andExpect(jsonPath("$.totalElements").value(0));

    }

    @Test
    void givenPosts_whenGetFind_thenCorrectResponse() throws Exception {
        //given
        User user = createUserAndAuthenticate();
        Post publishedAndActive1 = postCreator.createPost(user);
        Post publishedAndActive2 = postCreator.createPost(user, post -> post.setPublicationDate(null));
        Post publishedAndActive3 = postCreator.createPost(user);

        //not matching by deleted
        postCreator.createPost(user, post -> post.setStatus(PostStatus.DELETED));

        Post publishedAndActive4 = postCreator.createPost(user);

        // not matching by text
        postCreator.createPost(user,post -> post.setText("nie pasuje"));

        Post publishedAndActive5 = postCreator.createPost(user);

        //not matching not published
        Post notPublished = postCreator.createPost(user, post -> post.setPublicationDate(LocalDateTime.now().plusDays(1)));

        Post publishedAndActive6 = postCreator.createPost(user);

        Thread.sleep(1100);

        ResultActions resultActions = performGet(PATH_POST_URL,
                Map.of(
                        "q", "ex",
                        "page", "0",
                        "size", "3"
                )
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.content[*]", hasSize(3)))
                        .andExpect(jsonPath("$.content[*].id").value(contains(
                                publishedAndActive6.getId().intValue(),
                                publishedAndActive5.getId().intValue(),
                                publishedAndActive4.getId().intValue()))

                        );

    }
    //------findForLoggedUser

    @Test
    void givenNotAuthenticated_whenFindForLoggedUser_then401() throws Exception {
        //given


        FindPostRequest request = new FindPostRequest(null,null,null,null,null);


        ResultActions resultActions = performPost(PATH_POST_URL, request);

        //then
        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void givenNoPostInDb_whenFindForLoggedUser_thenEmptyList() throws Exception {

        //given
        createUserAndAuthenticate();

        String text = "Example Text";
        Set<PostStatus> status = Set.of(PostStatus.ACTIVE);
        FindPostRequest request = new FindPostRequest(status, text,null,null,null);

        String urlWithParams = PATH_POST_URL + "/findForLogged" + "?page=0&size=3";
        ResultActions resultActions = performPost(urlWithParams, request);

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

    }

    @Test
    void givenPostsForUserLogged_whenFindForLoggedUser_thenCorrectRequest() throws Exception {

        //given
        User user = createUserAndAuthenticate();

        String postText = "Example Text";


        Post post = new Post();
        post.setVersion(0);
        post.setCreatedDateTime(LocalDateTime.now().minusDays(1));
        post.setLastModifiedDateTime(LocalDateTime.now().minusDays(1));
        post.setText(postText);
        post.setAuthor(user.getLogin());
        post.setScope(PostScope.PUBLIC);
        post.setStatus(PostStatus.ACTIVE);
        post.setUser(user);
        postRepository.save(post);

        FindPostRequest request = new FindPostRequest(Set.of(PostStatus.ACTIVE),postText, null,null,null );


        String urlWithParams = PATH_POST_URL + "/findForLogged" + "?page=0&size=3";
        ResultActions resultActions = performPost(urlWithParams,request);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].text").value("Example Text"));

    }

}