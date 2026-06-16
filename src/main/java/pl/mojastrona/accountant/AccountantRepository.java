package pl.mojastrona.accountant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountantRepository extends CrudRepository<Accountant, Long>, JpaSpecificationExecutor<Accountant> {

    @Query("select a from Accountant a left join fetch a.clients where a.id=:accountantId")
    Optional<Accountant> findByAccountantIdFetchGroupsInfo(Long accountantId);

    @Query("select a from Accountant a join a.clients c where c.id = :clientId")
    Page<Accountant> find(Long clientId, PageRequest name);
}
