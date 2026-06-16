package pl.mojastrona.invoice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.invoice.detail.InvoiceDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static pl.mojastrona.util.LogUtil.logPage;

@Service
@Slf4j
public class InvoiceService {

    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void create(CreateInvoiceRequest invoiceRequest){

        Invoice invoice = new Invoice(

                invoiceRequest.paymentDate(),
                invoiceRequest.buyer(),
                invoiceRequest.seller()
        );

        Set<InvoiceDetail> invoiceDetails = new HashSet<>();
        invoiceDetails.add(InvoiceDetail.builder().productName("product1").price(new BigDecimal("500.00")).invoice(invoice).build());
        invoiceDetails.add(InvoiceDetail.builder().productName("product2").price(new BigDecimal("500.00")).invoice(invoice).build());
        invoice.setDetails(invoiceDetails);

        invoiceRepository.save(invoice);

    }

    public ReadInvoiceResponse findById(Long id){
        return invoiceRepository.findByIdFetchDetails(id)
                .map(ReadInvoiceResponse::from)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Invoice findByDetailId(Long id){
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
    }

    @Transactional
    public void update(Long id, @Valid UpdateInvoiceRequest updateInvoiceRequest) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Invoice newInvoice = new Invoice(invoice);
        newInvoice.setPaymentDate(updateInvoiceRequest.paymentDate());
        newInvoice.setBuyer(updateInvoiceRequest.buyer());
        newInvoice.setSeller(updateInvoiceRequest.seller());
        newInvoice.setVersion(updateInvoiceRequest.version());

//        Set<InvoiceDetail> invoiceDetails = newInvoice.getDetails();
//        InvoiceDetail invoiceDetail = invoiceDetails.iterator().next();
//        //invoiceDetail.setInvoice(null);
//        log.info("Usuwam ID o id = {}", invoiceDetail.getId());
//        invoiceDetail.setProductName("nowa wartosc");
//        // invoiceDetails.remove(invoiceDetail);
//        // ....
//
//        invoiceDetails.add(InvoiceDetail.builder().productName("productNew").price(BigDecimal.TEN).invoice(newInvoice).build());
        invoiceRepository.save(newInvoice);

    }

    @Transactional
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public void archive(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Invoice newInvoice = new Invoice(invoice);
        newInvoice.setStatus(InvoiceStatus.DELETED);
        invoiceRepository.save(newInvoice);
    }


    public Page<FindInvoiceResponse> find(String sellerContaining,
                                          String buyerContaining,
                                          int page,
                                          int size){
        return invoiceRepository.findByBuyerContainingAndSellerContainingAndStatusInOrderByPaymentDate(
                buyerContaining,
                sellerContaining,
                Set.of(InvoiceStatus.ACTIVE, InvoiceStatus.DRAFT),
                PageRequest.of(page, size)
        ).map(FindInvoiceResponse::from);
    }

    public Page<FindInvoiceResponse> find(FindInvoiceRequest findInvoiceRequest,Pageable pageable) {

        Specification<Invoice> invoiceSpecification = prepareSpecs(findInvoiceRequest);

        return invoiceRepository.findAll(invoiceSpecification, pageable)
                .map(FindInvoiceResponse::from);

    }

    public void find2() {


        log(() -> invoiceRepository.findByPaymentDateLessThanEqualOrderByPaymentDateDesc(
                        LocalDate.of(2025, 2, 28)
                ),
                "findByPaymentDateLessThanEqualOrderByPaymentDateDesc"

        );

        log(() -> invoiceRepository.findAndOrderByPaymentDateDesc(
                        LocalDate.of(2025, 2, 28)
                ),
                "findAndOrderByPaymentDateDesc"

        );

        log(() ->invoiceRepository.findByPaymentDateLessThanEqual(
                        LocalDate.of(2025, 3, 31),
                        Sort.by(Sort.Order.desc("paymentDate"),
                                Sort.Order.asc("id") )),
                "findByPaymentDateLessThanEqual"
        );


        log(() -> invoiceRepository.findByPaymentDateBetweenAndSellerStartingWithIgnoreCaseAndStatusIn(
                LocalDate.of(2025,2,1),
                LocalDate.of(2025,2,28),
                "sel",
                Set.of(InvoiceStatus.ACTIVE, InvoiceStatus.DELETED)
        ),"findByPaymentDateBetweenAndSellerStartingWithIgnoreCaseAndStatusIn");

        log(() -> invoiceRepository.findBy(
                LocalDate.of(2025,2,1),
                LocalDate.of(2025,2,28),
                "sel%",
                Set.of(InvoiceStatus.ACTIVE, InvoiceStatus.DELETED)
        ),"findBy");

        log(() ->invoiceRepository.findByAndSort(
                        LocalDate.of(2025, 3, 31),
                        Sort.by(Sort.Order.desc("paymentDate"),
                                Sort.Order.asc("id") )),
                "findByAndSort"
        );

        logPage(() -> invoiceRepository.findAllByAndSort(LocalDate.of(2025, 3, 31),
                PageRequest.of(0,3, Sort.by(Sort.Order.asc("seller")))),
                "findAllByAndSortPage0");

        logPage(() -> invoiceRepository.findAllByAndSort(LocalDate.of(2025, 3, 31),
                PageRequest.of(1,3, Sort.by(Sort.Order.asc("seller")))),
                "findAllByAndSortPage1");

        logPage(() -> invoiceRepository.findAllByAndSort(LocalDate.of(2025, 3, 31),
                PageRequest.of(2,3, Sort.by(Sort.Order.asc("seller")))),
                "findAllByAndSortPage2");

        logPage(() -> invoiceRepository.findAllByAndSort(LocalDate.of(2025, 3, 31),
                PageRequest.of(3,3, Sort.by(Sort.Order.asc("seller")))),
                "findAllByAndSortPage3");




    }

    private void log(Supplier<List<Invoice>> listSupplier, String methodName){

        System.out.println("---------" + methodName + "---------");
        listSupplier.get().forEach(System.out::println);
    }

    private static Specification<Invoice> prepareSpecs(FindInvoiceRequest findInvoiceRequest) {

        Specification<Invoice> specification = Specification.where(null);

        if(findInvoiceRequest.paymentDateMin() !=null && findInvoiceRequest.paymentDateMax() != null){
            specification = specification.and((root, query, cr) ->
                    cr.between(root.get("paymentDate"),
                            findInvoiceRequest.paymentDateMin(),
                            findInvoiceRequest.paymentDateMax()));
        }


        if(findInvoiceRequest.seller() != null){
            specification = specification.and((root, query, cr) ->
                    likeIgnoreCase(cr,root.get("seller"),"%"+ findInvoiceRequest.seller()+ "%"));

        }


        if(findInvoiceRequest.invoiceStatuses() != null){
            specification = specification.and((root, query, cr) ->
                    (root.get("status").in(findInvoiceRequest.invoiceStatuses())));
            

        }
        return specification;
    }

    private static Specification<Invoice> prepareSpecsUsingPredicates(FindInvoiceRequest findInvoiceRequest) {
        return (root, query, cr) -> {

            List<Predicate> predicates = new ArrayList<>();
            if(findInvoiceRequest.paymentDateMin() != null && findInvoiceRequest.paymentDateMax() != null){
                predicates.add(cr.between(root.get("paymentDate"),
                        findInvoiceRequest.paymentDateMin(),
                        findInvoiceRequest.paymentDateMax()));
            }

            if(findInvoiceRequest.seller() != null){
                predicates.add(likeIgnoreCase(cr, root.get("seller"), findInvoiceRequest.seller()));

            }

            if(findInvoiceRequest.invoiceStatuses() != null){
                predicates.add(root.get("status").in(findInvoiceRequest.invoiceStatuses()));
            }



            return cr.and(predicates.toArray(Predicate[]::new));

        };

    }

    private static Predicate likeIgnoreCase(CriteriaBuilder cr, Path<String> fieldPath, String text) {
        return cr.like(cr.lower(fieldPath), "%" + text.toLowerCase() + "%");
    }


}

