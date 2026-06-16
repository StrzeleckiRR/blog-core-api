package pl.mojastrona.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.security.access.AccessDeniedException;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserRole;



class CommentAuthorizationCheckerTest extends BaseUnitTest {

    @InjectMocks
    private CommentAuthorizationChecker underTest;

    @ParameterizedTest
    @CsvSource({
            "10, ADMIN",
            "10, USER",
            "231, ADMIN"
    })
    void givenAdminOrAuthor_whenCheckPermissions_thenDoesNotThrow(Long id, UserRole role) {
        //given
        long authorId = 10L;

        User loggedUser = User.builder()
                .id(id)
                .role(role)
                .build();

        User author = User.builder()
                .id(authorId)
                .build();

        Comment comment = Comment.builder()
                .id(12L)
                .user(author)
                .build();


        //when
        Executable ex = () -> underTest.checkPermissions(loggedUser, comment);

        //then
        Assertions.assertDoesNotThrow(ex);
    }

    @Test
    void givenNotAdminAndAuthor_whenCheckPermissions_thenThrowAccessDenied() {
        //given
        long authorId = 10L;

        User loggedUser = User.builder()
                .id(authorId+10)
                .role(UserRole.USER)
                .build();

        User author = User.builder()
                .id(authorId)
                .build();

        Comment comment = Comment.builder()
                .id(12L)
                .user(author)
                .build();


        //when
        Executable ex = () -> underTest.checkPermissions(loggedUser, comment);

        //then
        Assertions.assertThrows(AccessDeniedException.class, ex);
    }
}