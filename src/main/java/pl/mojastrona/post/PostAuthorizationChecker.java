package pl.mojastrona.post;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import pl.mojastrona.user.User;

@Component
public class PostAuthorizationChecker {

    public void checkPermissions(User user, Post post) {
        if (!user.isAdmin() && !post.isAuthor(user.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this post");
        }
    }
}
