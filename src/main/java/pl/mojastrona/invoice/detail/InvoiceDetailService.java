package pl.mojastrona.invoice.detail;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.invoice.Invoice;
import pl.mojastrona.invoice.InvoiceService;
import pl.mojastrona.util.SpecificationUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceDetailService {

    private final InvoiceService invoiceService;
    private final InvoiceDetailRepository detailRepository;

    @Transactional
    public void create(CreateInvoiceDetailRequest invoiceDetailRequest){

        Invoice invoice = invoiceService.findByDetailId(invoiceDetailRequest.getInvoiceId());

        InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                .productName(invoiceDetailRequest.getProductName())
                .price(invoiceDetailRequest.getPrice())
                .invoice(invoice)
                .build();
        log.debug("invoiceDetail: {}", invoiceDetail);

        log.info("zapisano: {}", detailRepository.save(invoiceDetail));

    }

    @Transactional
    public ReadInvoiceDetailResponse findById(Long id) {
        Optional<InvoiceDetail> maybeDetails = detailRepository.findByIdFetchInvoice(id);
        Optional<ReadInvoiceDetailResponse> readInvoiceDetailResponse = maybeDetails.map(ReadInvoiceDetailResponse::from);
        ReadInvoiceDetailResponse details = readInvoiceDetailResponse.orElseThrow(EntityNotFoundException::new);
        return details;

    }

    @Transactional
    public void update(Long id, @Valid UpdateInvoiceDetailRequest updateInvoiceRequest) {

        InvoiceDetail invoiceDetail = detailRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        InvoiceDetail newDetail = invoiceDetail.toBuilder()
                .version(updateInvoiceRequest.getVersion())
                .productName(updateInvoiceRequest.getProductName())
                .price(updateInvoiceRequest.getPrice())
                .createdDate(invoiceDetail.getCreatedDate())
                .lastModifiedDateTime(invoiceDetail.getLastModifiedDateTime())
                .invoice(invoiceDetail.getInvoice())
                .build();

//        InvoiceDetail newDetail = new InvoiceDetail(
//                invoiceDetail.getId(),
//                updateInvoiceRequest.getVersion(),
//                updateInvoiceRequest.getProductName(),
//                updateInvoiceRequest.getPrice(),
//                invoiceDetail.getCreatedDate(),
//                invoiceDetail.getLastModifiedDateTime(),
//                invoiceDetail.getInvoice());

        detailRepository.save(newDetail);
    }

    public Page<ReadInvoiceDetailResponse> find(Long invoiceId, Pageable pageable) {
        return detailRepository.findAll(prepareSpecification(invoiceId), pageable).
                map(ReadInvoiceDetailResponse::from);
    }

    private Specification<InvoiceDetail> prepareSpecification(Long invoiceId) {
        return (root, query, criteriaBuilder) -> {
            if(!SpecificationUtil.isABoolean(query)){
                root.fetch("invoice");
            }
            return criteriaBuilder.equal(root.get("invoice").get("id"), invoiceId);
        };
    }
}
