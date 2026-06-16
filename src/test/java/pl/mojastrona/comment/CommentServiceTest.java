package pl.mojastrona.comment;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.post.*;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static pl.mojastrona.comment.ReadCommentResponse.*;

class CommentServiceTest extends BaseUnitTest {

    @InjectMocks
    private CommentService underTest;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LoggedUserProvider loggedUserProvider;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Mock
    private PostService postService;

    @Test
    void givenCorrectRequest_whenCreate_thenCreateComment() {

        String expectedText = "Jakiś komentarz";

        long expectedPostId = 9L;
        long expectedUserId = 8L;
        CreateCommentRequest request = new CreateCommentRequest(
                expectedText,
                expectedPostId
        );

        String expLogin = "login";
        User user = User.builder()
                .id(expectedUserId)
                .login(expLogin)
                .password("password")
                .build();

        Post post = Post.builder()
                .id(expectedPostId)
                .build();

        Mockito.when(loggedUserProvider.provideLoggedUser()).thenReturn(user);
        Mockito.when(postService.findPostById(post.getId())).thenReturn(post);

        underTest.create(request);

        Mockito.verify(commentRepository).save(commentCaptor.capture());

        Comment comment = commentCaptor.getValue();
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isNull();
        assertThat(comment.getCreatedDateTime()).isNull();
        assertThat(comment.getLastModifiedDateTime()).isNull();
        assertThat(comment.getText()).isEqualTo(request.getText());
        assertThat(comment.getAuthor()).isEqualTo(expLogin);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getUser()).isEqualTo(user);

    }

    @Test
    void givenCommentIdNotExists_whenFindById_thenEntityNotFoundException() {

        //given
        Long expectedCommentId = 67L;


        //when
        Executable executable = () -> underTest.findById(expectedCommentId);


        //then
        Assertions.assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void givenCommentExists_whenFindById_thenReturnResponse() {

        //given
        Long expectedCommentId = 67L;
        Long expectedPostId = 123L;

        String expectedCommentText = "tekst tekst";
        String expectedPostText = "postTest";
        String expectedPostAuthor = "PostAuthor";
        String expectedCommentAuthor = "Marcin Strzelecki";

        LocalDateTime expectedPostCreatedDateTime = LocalDateTime.of(2026, 5, 11, 15, 12, 32);
        LocalDateTime expectedCommentCreatedDateTime = LocalDateTime.now();

        Post post = Post.builder()
                .id(expectedPostId)
                .version(0)
                .text(expectedPostText)
                .author(expectedPostAuthor)
                .createdDateTime(expectedPostCreatedDateTime)
                .publicationDate(LocalDateTime.now())
                .scope(PostScope.PUBLIC)
                .status(PostStatus.ACTIVE)
                .build();

        Comment comment = Comment.builder()
                .id(expectedCommentId)
                .text(expectedCommentText)
                .createdDateTime(expectedCommentCreatedDateTime)
                .author(expectedCommentAuthor)
                .post(post)
                .build();

        Mockito.when(commentRepository.findByIdFetchPost(expectedCommentId))
                .thenReturn(Optional.of(comment));


        //when
        ReadCommentResponse response = underTest.findById(expectedCommentId);


        //then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(expectedCommentId);
        assertThat(response.getText()).isEqualTo(expectedCommentText);
        assertThat(response.getCreatedDateTime()).isEqualToIgnoringNanos(expectedCommentCreatedDateTime);

        assertThat(response).extracting("id", "text")
                .containsExactly(expectedCommentId, expectedCommentText);

        assertThat(response).extracting(ReadCommentResponse::getId, ReadCommentResponse::getText)
                .containsExactly(expectedCommentId, expectedCommentText);


        PostResponse responsePost = response.getPost();
        assertThat(responsePost).isNotNull();
        assertThat(responsePost.getId()).isEqualTo(expectedPostId);
        assertThat(responsePost.getText()).isEqualTo(expectedPostText);
        assertThat(responsePost.getCreatedDateTime()).isEqualToIgnoringNanos(expectedPostCreatedDateTime); // ignorowanie nanosekund

        assertThat(responsePost).extracting(PostResponse::getId, PostResponse::getText, PostResponse::getAuthor)
                .containsExactly(expectedPostId, expectedPostText, expectedPostAuthor);
    }
}