package pl.mojastrona.comment;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> , JpaSpecificationExecutor<Comment> {

    @Query("select c from Comment c join fetch c.post where c.id=:id")
    Optional<Comment> findByIdFetchPost(Long id);
}
