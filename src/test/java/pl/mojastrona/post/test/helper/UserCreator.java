package pl.mojastrona.post.test.helper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.post.Post;
import pl.mojastrona.post.PostScope;
import pl.mojastrona.post.PostStatus;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserRole;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

@Component
public class UserCreator {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser() {
        return createUser("user", UserRole.USER);
    }

    @Transactional
    public User createAdmin() {
        return createUser("admin", UserRole.ADMIN);
    }

    @Transactional
    public User createUser(String login, UserRole role) {

        User user = User.builder()
                .login(login)
                .role(role)
                .password(passwordEncoder.encode("test123"))
                .build();

        entityManager.persist(user);
        return user;
    }
}
