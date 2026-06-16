package pl.mojastrona.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InvoiceRepository extends CrudRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    List<Invoice> findByPaymentDateLessThanEqualOrderByPaymentDateDesc(LocalDate date);

    Page<Invoice> findByBuyerContainingAndSellerContainingAndStatusInOrderByPaymentDate(
                                                                      String buyer,
                                                                      String seller,
                                                                      Set<InvoiceStatus> invoiceStatuses,
                                                                      Pageable pageable
    );

    @Query("select i from Invoice i where i.paymentDate <= :date order by i.paymentDate desc")
    List<Invoice> findAndOrderByPaymentDateDesc(LocalDate date);

    List<Invoice> findByPaymentDateLessThanEqual(LocalDate date, Sort sort);

    @Query("select i from Invoice i where i.paymentDate <= :date")
    List<Invoice> findByAndSort(LocalDate date, Sort sort);

    @Query("select i from Invoice i where i.paymentDate <= :date")
    Page<Invoice> findAllByAndSort(LocalDate date, Pageable pageable);

    List<Invoice> findByPaymentDateBetweenAndSellerStartingWithIgnoreCaseAndStatusIn(LocalDate startDate,
                                                                                     LocalDate endDate,
                                                                                     String seller,
                                                                                     Set<InvoiceStatus> invoiceStatuses);

    @Query("select i from Invoice i where i.paymentDate between :startDate and :endDate" +
            " and lower (i.seller) like lower (:seller)" +
            " and i.status in :invoiceStatuses")
    List<Invoice> findBy(LocalDate startDate, LocalDate endDate,
                         String seller,
                         Set<InvoiceStatus> invoiceStatuses);

    @Query("select i from Invoice i left join fetch i.details where i.id = :id")
    Optional<Invoice> findByIdFetchDetails(Long id);
}
