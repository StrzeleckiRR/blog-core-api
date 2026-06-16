package pl.mojastrona.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    @Query("select c from Client c join c.accountants a where a.id = :accountantId")
    Page<Client> find(Long accountantId, PageRequest name);
}
