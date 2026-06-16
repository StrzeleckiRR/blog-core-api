package pl.mojastrona.comment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.post.Post;
import pl.mojastrona.post.PostService;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;
import pl.mojastrona.util.SpecificationUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostService postService;

    private final LoggedUserProvider loggedUserProvider;

    private final CommentAuthorizationChecker commentAuthorizationChecker;


    @Transactional
    public void create(CreateCommentRequest commentRequest){

        User user = loggedUserProvider.provideLoggedUser();

        Post post = postService.findPostById(commentRequest.getPostId());

        Comment comment = Comment.builder()
                .text(commentRequest.getText())
                .author(user.getLogin())
                .post(post)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    //@Transactional(readOnly = true)
    public ReadCommentResponse findById(Long id) {

        Optional<Comment> maybeComment = commentRepository.findByIdFetchPost(id);
        log.debug("maybeComment: {}", maybeComment);
        Optional<ReadCommentResponse> readCommentResponse = maybeComment.map(ReadCommentResponse::from);
        log.debug("readCommentResponse: {}", readCommentResponse);

        ReadCommentResponse comment = readCommentResponse.orElseThrow(EntityNotFoundException::new);
        log.debug("comment: {}", comment);

        return comment;
    }


    @Transactional
    public void update(Long id, @Valid UpdateCommentRequest updateCommentRequest) {

        User user = loggedUserProvider.provideLoggedUser();

        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        commentAuthorizationChecker.checkPermissions(user, comment);

        Comment newComment = comment.toBuilder()
                .text(updateCommentRequest.getText())
                .author(updateCommentRequest.getAuthor())
                .build();
        

        commentRepository.save(newComment);
    }

    public Page<ReadCommentResponse> find(Long postId, Pageable pageable) {
        return commentRepository.findAll(prepareSpecification(postId), pageable).
                map(ReadCommentResponse::from);
    }

    private Specification<Comment> prepareSpecification(Long postId) {

        return (root, query, criteriaBuilder) -> {
            if(!SpecificationUtil.isABoolean(query)){
                root.fetch("post");
            }
          return criteriaBuilder.equal(root.get("post").get("id"), postId);
        };
    }
}
