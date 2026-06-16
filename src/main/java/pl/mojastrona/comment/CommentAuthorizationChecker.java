package pl.mojastrona.comment;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import pl.mojastrona.user.User;

@Component
public class CommentAuthorizationChecker {
    public void checkPermissions(User user, Comment comment) {
        if (!user.isAdmin() && !comment.isAuthor(user.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this post");
        }
    }
}
