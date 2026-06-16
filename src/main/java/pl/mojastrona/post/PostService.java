package pl.mojastrona.post;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.security.LoggedUserProvider;
import pl.mojastrona.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static pl.mojastrona.util.LogUtil.logPage;

@Service
public class PostService {

    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    private final PostRepository postRepository;

    private final LoggedUserProvider loggedUserProvider;

    private final PostAuthorizationChecker postAuthorizationChecker;

    public PostService(PostRepository postRepository, LoggedUserProvider loggedUserProvider, PostAuthorizationChecker postAuthorizationChecker) {
        this.postRepository = postRepository;
        this.loggedUserProvider = loggedUserProvider;
        this.postAuthorizationChecker = postAuthorizationChecker;
    }


    @Transactional
    public void create(CreatePostRequest postRequest) {

        User user = loggedUserProvider.provideLoggedUser();

        Post post = new Post(
                postRequest.getText(),
                user.getLogin(),
                postRequest.getScope(),
                postRequest.getPublicationDate(),
                user

        );
        postRepository.save(post);

    }

    public ReadPostResponse findById(Long id){
        return postRepository.findByIdFetchComments(id)
                .map(ReadPostResponse::from)
                .orElseThrow(EntityNotFoundException::new);

    }

    public Post findPostById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

    }

    @Transactional
    public void update(Long id, @Valid UpdatePostRequest updatePostRequest){

        User user = loggedUserProvider.provideLoggedUser();

        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        postAuthorizationChecker.checkPermissions(user, post);

        Post newPost = new Post(post);
            newPost.setText(updatePostRequest.getText());
            newPost.setScope(updatePostRequest.getScope());
            newPost.setVersion(updatePostRequest.getVersion());

            postRepository.save(newPost);




    }

    @Transactional
    public void delete(Long id) {

        User user = loggedUserProvider.provideLoggedUser();
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        postAuthorizationChecker.checkPermissions(user, post);

        postRepository.deleteById(id);
    }

    @Transactional
    public void archive(Long id) {

        User user = loggedUserProvider.provideLoggedUser();
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        postAuthorizationChecker.checkPermissions(user, post);


        Post newPost = new Post(post);
        newPost.setStatus(PostStatus.DELETED);
        postRepository.save(newPost);
    }

    public Page<FindPostResponse> find(FindPostRequest findPostRequest, Pageable pageable) {

        Specification<Post> specification = preparePostSpecification(findPostRequest);
        return postRepository.findAll(specification, pageable)
                .map(FindPostResponse::from);

    }

    private Specification<Post> preparePostSpecification(FindPostRequest findPostRequest) {

        Specification<Post> specification = Specification.where(null);

        if(findPostRequest.postStatuses()!=null){
            Specification<Post> statusInSpec = (root, query, criteriaBuilder) ->
                    root.get("status").in(findPostRequest.postStatuses());
            specification = specification.and(statusInSpec);
        }

        if(findPostRequest.text()!=null){
            Specification<Post> textLikeSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("text")), "%" + findPostRequest.text().toLowerCase() + "%");
            specification = specification.and(textLikeSpec);
        }


        if(findPostRequest.publicationDate()!=null) {
            Specification<Post> publicationDateSpec = (root, query, criteriaBuilder) ->
            {
                Predicate publicationDateIsNull = criteriaBuilder.isNull(root.get("publicationDate"));
                Predicate publicationDateLEPred = criteriaBuilder.lessThanOrEqualTo(root.get("publicationDate"), findPostRequest.publicationDate());
                return criteriaBuilder.or(publicationDateIsNull, publicationDateLEPred);
            };
            specification = specification.and(publicationDateSpec);
        }


        if(findPostRequest.createDateTimeMin()!=null && findPostRequest.createDateTimeMax()!=null) {
            Specification<Post> createDateTimeBetween = (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("createdDateTime"),
                            findPostRequest.createDateTimeMin(),
                            findPostRequest.createDateTimeMax());
            specification = specification.and(createDateTimeBetween);
        }


        return specification;
    }

    private Specification<Post> preparePostSpecificationUsingPredicates(FindPostRequest findPostRequest) {

        return (root, query, criteriaBuilder) ->
        {

            List<Predicate> predicates = new ArrayList<>();
            if(findPostRequest.postStatuses()!=null){
                predicates.add(root.get("status").in(findPostRequest.postStatuses()));
            }

            if(findPostRequest.text()!=null){
                predicates.add(criteriaBuilder.like(root.get("text"), "%" + findPostRequest.text() + "%"));
            }

            if(findPostRequest.publicationDate()!=null){
                Predicate publicationDateIsNull = criteriaBuilder.isNull(root.get("publicationDate"));
                Predicate publicationDateLEPred = criteriaBuilder.lessThanOrEqualTo(root.get("publicationDate"), findPostRequest.publicationDate());
                predicates.add(criteriaBuilder.or(publicationDateIsNull, publicationDateLEPred));
            }

            if(findPostRequest.createDateTimeMax()!=null && findPostRequest.createDateTimeMin()!=null) {
                predicates.add(criteriaBuilder.between(root.get("createdDateTime"),
                        findPostRequest.createDateTimeMin(),
                        findPostRequest.createDateTimeMax()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Page<FindPostResponse> find(String textContaining,
                                       int page,
                                       int size){

        return postRepository.findActiveAndPublished(textContaining,
                        LocalDateTime.now(),
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDateTime"))))
                .map(FindPostResponse::from);
    }

    public void find2() {


        log(postRepository.findByStatusOrderByCreatedDateTimeDesc(PostStatus.ACTIVE), "findByStatusOrderByCreatedDateTimeDesc");

        log(postRepository.findByStatus(PostStatus.ACTIVE, Sort.by("createdDateTime", "author")), "findByStatus");

        log(postRepository.findByStatus(PostStatus.ACTIVE, Sort.by(
               Sort.Order.asc("createdDateTime"),
                Sort.Order.desc("author"))),
                 "findByStatus");

       System.out.println(postRepository.countByStatus(PostStatus.DELETED));
       System.out.println(postRepository.existsByStatus(PostStatus.ACTIVE));

        log(postRepository.findByStatusInAndAuthorLike(Set.of(PostStatus.DELETED), "Cristiano Ronaldo"), "findByStatusInAndAuthorLike");
        log(postRepository.findByStatusInAndAuthorContaining(Set.of(PostStatus.DELETED), "Cristiano Ronaldo"), "findByStatusInAndAuthorContaining");
        log(postRepository.findByStatusInAndAuthorStartingWith(Set.of(PostStatus.DELETED), "Cristiano Ronaldo"), "findByStatusInAndAuthorStartingWith");


        log(postRepository.findByStatusInAndCreatedDateTimeBetween(Set.of(PostStatus.DELETED),
                LocalDate.of(2025, 3,3).atStartOfDay(),
                LocalDate.of(2025,3,10).atStartOfDay()),
                "findByStatusInAndCreatedDateTimeBetween");

        log(postRepository::find, "find");

        log(() -> postRepository.findOrderByCreatedDateTimeDesc(PostStatus.ACTIVE), "findOrderByCreatedDateTimeDesc");

        log(() -> postRepository.findByStatus(PostStatus.DELETED, Sort.by(
                        Sort.Order.desc("createdDateTime"),
                        Sort.Order.desc("author"))),
                "findByStatus");

        log(() -> postRepository.findAndSort(PostStatus.DELETED, Sort.by(
                        Sort.Order.desc("createdDateTime"),
                        Sort.Order.desc("author"))),
                "findAndSort");

        log(() -> postRepository.findByStatusInAndAuthorLike(
                Set.of(PostStatus.DELETED),
                "Cristiano Ronaldo"),
                "findByStatusInAndAuthorLike");

        log(() -> postRepository.find(
                        Set.of(PostStatus.DELETED),
                        "Ronaldo"),
                "find");

        log( () -> postRepository.findByStatus(PostStatus.ACTIVE, PageRequest.of(0,2, Sort.by(Sort.Order.desc("id")))), "findByStatus");
        log( () -> postRepository.findByStatus(PostStatus.ACTIVE, PageRequest.of(1,2, Sort.by(Sort.Order.desc("id")))), "findByStatus");
        log( () -> postRepository.findByStatus(PostStatus.ACTIVE, PageRequest.of(2,2, Sort.by(Sort.Order.desc("id")))), "findByStatus");
        log( () -> postRepository.findByStatus(PostStatus.ACTIVE, PageRequest.of(3,2, Sort.by(Sort.Order.desc("id")))), "findByStatus");

        logPage( () -> postRepository.findAllByStatus(PostStatus.ACTIVE, PageRequest.of(0,2, Sort.by(Sort.Order.desc("id")))), "findAllByStatusPage 0");
        logPage( () -> postRepository.findAllByStatus(PostStatus.ACTIVE, PageRequest.of(1,2, Sort.by(Sort.Order.desc("id")))), "findAllByStatusPage 1");
        logPage( () -> postRepository.findAllByStatus(PostStatus.ACTIVE, PageRequest.of(2,2, Sort.by(Sort.Order.desc("id")))), "findAllByStatusPage 2");
        logPage( () -> postRepository.findAllByStatus(PostStatus.ACTIVE, PageRequest.of(3,2, Sort.by(Sort.Order.desc("id")))), "findAllByStatusPage 3");

    }

    private void log(List<Post> posts, String methodName){
        System.out.println("------------ "+ methodName + " --------------");
        posts.forEach(System.out::println);
    }

    private void log(Supplier<List<Post>> listSupplier, String methodName){

        System.out.println("---------" + methodName + "---------");
        listSupplier.get().forEach(System.out::println);
    }
}
