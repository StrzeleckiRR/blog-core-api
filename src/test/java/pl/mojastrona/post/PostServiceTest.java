package pl.mojastrona.post;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

class PostServiceTest extends BaseUnitTest {

    @InjectMocks
    private PostService underTest;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LoggedUserProvider loggedUserProvider;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Test
    void givenCorrectRequest_whenCreate_thenCreatePost() {

        String expectedText = "text";
        PostScope expectedPostScape = PostScope.PUBLIC;
        LocalDateTime expectedPublicationDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);

        CreatePostRequest request = new CreatePostRequest(
                expectedText,
                expectedPostScape,
                expectedPublicationDate
        );

        String expLogin = "login";
        User user = User.builder()
                .id(23L)
                .login(expLogin)
                .password("password")
                .build();

        Mockito.when(loggedUserProvider.provideLoggedUser()).thenReturn(user);

        underTest.create(request);

        Mockito.verify(postRepository).save(postCaptor.capture());

        Post post = postCaptor.getValue();
        assertThat(post).isNotNull();
        assertThat(post.getVersion()).isNull();
        assertThat(post.getCreatedDateTime()).isNull();
        assertThat(post.getLastModifiedDateTime()).isNull();
        assertThat(post.getText()).isEqualTo(expectedText);
        assertThat(post.getPublicationDate()).isEqualToIgnoringNanos(expectedPublicationDate);
        assertThat(post.getScope()).isEqualTo(expectedPostScape);
        assertThat(post.getStatus()).isEqualTo(PostStatus.ACTIVE);
        assertThat(post.getAuthor()).isEqualTo(expLogin);
        assertThat(post.getUser()).isEqualTo(user);

    }

    @Test
    void givenNoResults_whenFindAll_thenReturnEmptyPage() {

        FindPostRequest request = new FindPostRequest(null,null,null
                ,null,null);

        int expectedPageSize = 10;
        Pageable pageable = Pageable.ofSize(expectedPageSize);

        Mockito.when(postRepository.findAll(Mockito.any(), eq(pageable))).thenReturn(
                Page.empty(pageable)
        );
        Page<FindPostResponse> response = underTest.find(request, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getSize()).isEqualTo(expectedPageSize);


    }

    @Test
    void givenTwoResults_whenFindAll_thenReturnResponseInCorrectOrder() {

        FindPostRequest request = new FindPostRequest(null,null,null
                ,null,null);

        int expectedPageSize = 10;

        long post1Id = 1L;
        long post2Id = 2L;

        Pageable pageable = Pageable.ofSize(expectedPageSize);

        Post post1 = Post.builder()
                .id(post1Id)
                .comments(Set.of())
                .build();

        Post post2 = Post.builder()
                .id(post2Id)
                .comments(Set.of())
                .build();

        List<Post> postList = List.of(post1, post2);

        Mockito.when(postRepository.findAll(Mockito.any(), eq(pageable))).thenReturn(
                new PageImpl<>(
                        postList, pageable, postList.size())
                );
        Page<FindPostResponse> response = underTest.find(request, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent()).
                extracting(FindPostResponse::id)
                        .containsExactly(post1Id,post2Id);
        assertThat(response.getSize()).isEqualTo(expectedPageSize);


    }
}