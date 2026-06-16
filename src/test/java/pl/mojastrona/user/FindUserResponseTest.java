package pl.mojastrona.user;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class FindUserResponseTest {

    @Test
    void givenUser_whenFrom_thanCorrectResponse() {
        //given dajemy
        long expectedId = 67L;
        String expectedLogin = "login";

        User user = User.builder()
                .id(expectedId)
                .login(expectedLogin)
                .build();
        //when kiedy
        FindUserResponse response = FindUserResponse.from(user);

        //then oczekujemy
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(expectedId, response.getId());
//        Assertions.assertEquals(expectedLogin, response.getLogin());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(expectedId);
        assertThat(response.getLogin()).isEqualTo(expectedLogin);
    }
}