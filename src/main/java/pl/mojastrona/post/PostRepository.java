package pl.mojastrona.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query("select p from Post p")
    List<Post> find();

    @Query("select p from Post p left join fetch p.comments where p.id=:id")
    Optional<Post> findByIdFetchComments(Long id);

    @Query("select p from Post p where p.text like %:textContaining%" +
    " and p.status='ACTIVE'" +
    " and ( p.publicationDate is null or p.publicationDate <= :publicationDate )")
    Page<Post> findActiveAndPublished(String textContaining, LocalDateTime publicationDate, Pageable pageable);

    List<Post> findByStatusOrderByCreatedDateTimeDesc(PostStatus postStatus);

    @Query("select p from Post p where p.status=:postStatus order by p.createdDateTime DESC")
    List<Post> findOrderByCreatedDateTimeDesc(PostStatus postStatus);

    List<Post> findByStatus(PostStatus postStatus, Sort sort);

    List<Post> findByStatus(PostStatus postStatus, Pageable pageable);

    Page<Post> findAllByStatus(PostStatus postStatus, Pageable pageable);

    @Query("select p from Post p where p.status=:postStatus")
    List<Post> findAndSort(PostStatus postStatus, Sort sort);

    @Query("select p from Post p where p.status=:postStatus")
    Page<Post> findWithPaging(PostStatus postStatus, Pageable pageable);

    List<Post> findByStatusAndAuthor(PostStatus postStatus, String author);

    List<Post> findByStatusInAndAuthorLike(Set<PostStatus> postStatuses, String author);

    @Query("select p from Post p where p.status in :postStatuses and p.author like %:author")
    List<Post> find(Set<PostStatus> postStatuses, String author);

    List<Post> findByStatusInAndAuthorContaining(Set<PostStatus> postStatuses, String author);

    List<Post> findByStatusInAndAuthorStartingWith(Set<PostStatus> postStatuses, String author);

    List<Post> findByStatusInAndCreatedDateTimeBetween(Set<PostStatus> postStatuses, LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(PostStatus postStatus);

    boolean existsByStatus(PostStatus postStatus);
}
