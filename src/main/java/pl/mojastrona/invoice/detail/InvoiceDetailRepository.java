package pl.mojastrona.invoice.detail;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvoiceDetailRepository extends CrudRepository<InvoiceDetail, Long>, JpaSpecificationExecutor<InvoiceDetail> {
    @Query("select id from InvoiceDetail id join fetch id.invoice where id.id=:id")
    Optional<InvoiceDetail> findByIdFetchInvoice(Long id);
}
