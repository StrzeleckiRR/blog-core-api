package pl.mojastrona.invoice.detail;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.invoice.Invoice;
import pl.mojastrona.invoice.InvoiceService;
import pl.mojastrona.invoice.InvoiceStatus;
import pl.mojastrona.invoice.ReadInvoiceResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceDetailServiceTest extends BaseUnitTest {

    @InjectMocks
    private InvoiceDetailService underTest;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private InvoiceDetailRepository detailRepository;

    @Test
    void givenInvoiceDetailIdNotExist_whenFindById_thenEntityNotFoundException() {

        Long expectedId = 107L;

        Mockito.when(detailRepository.findByIdFetchInvoice(expectedId)).thenReturn(Optional.empty());

        Executable executable = () -> underTest.findById(expectedId);

        Assertions.assertThrows(EntityNotFoundException.class, executable);

    }

    @Test
    void givenInvoiceDetailExist_whenFindById_thenReturnResponse() {

        Long expectedInvoiceDetailId = 65L;
        int expectedInvoiceDetailVersion = 0;

        LocalDateTime expectedInvoiceDetailCreateDateTime = LocalDateTime.now();

        String expectedInvoiceDetailProductName = "Piekarnik";

        BigDecimal expectedInvoiceDetailPrice = BigDecimal.valueOf(1299.00);


        long expectedInvoiceId = 123L;
        int expectedInvoiceVersion = 0;

        String expectedInvoiceBuyerName = "Marcin Strzelecki";
        String expectedInvoiceSellerName = "Seller 1";

        LocalDateTime expectedInvoiceCreateDateTime = LocalDateTime.now();
        LocalDate expectedInvoicePaymentDate = LocalDate.now();

        InvoiceStatus expectedInvoiceStatus = InvoiceStatus.DELETED;
        Invoice invoice = Invoice.builder()
                .id(expectedInvoiceId)
                .version(expectedInvoiceVersion)
                .createdDate(expectedInvoiceCreateDateTime)
                .paymentDate(expectedInvoicePaymentDate)
                .buyer(expectedInvoiceBuyerName)
                .seller(expectedInvoiceSellerName)
                .status(expectedInvoiceStatus)
                .details(new HashSet<>())
                .build();

        InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                .id(expectedInvoiceDetailId)
                .version(expectedInvoiceDetailVersion)
                .productName(expectedInvoiceDetailProductName)
                .createdDate(expectedInvoiceDetailCreateDateTime)
                .price(expectedInvoiceDetailPrice)
                .invoice(invoice)
                .build();


        Mockito.when(detailRepository.findByIdFetchInvoice(expectedInvoiceDetailId)).thenReturn(Optional.of(invoiceDetail));

        ReadInvoiceDetailResponse invoiceDetailsResponse = underTest.findById(expectedInvoiceDetailId);

        assertThat(invoiceDetailsResponse).isNotNull();

        ReadInvoiceResponse invoiceResponse = invoiceDetailsResponse.getInvoice();
        assertThat(invoiceResponse).isNotNull();

        assertThat(invoiceDetailsResponse).extracting(
                        ReadInvoiceDetailResponse::getId,
                        ReadInvoiceDetailResponse::getVersion,
                        ReadInvoiceDetailResponse::getCreatedDate,
                        ReadInvoiceDetailResponse::getProductName,
                        ReadInvoiceDetailResponse::getPrice)
                .containsExactly(expectedInvoiceDetailId,
                        expectedInvoiceDetailVersion,
                        expectedInvoiceDetailCreateDateTime,
                        expectedInvoiceDetailProductName,
                        expectedInvoiceDetailPrice);

        assertThat(invoiceResponse).extracting(
                        ReadInvoiceResponse::getId,
                        ReadInvoiceResponse::getVersion,
                        ReadInvoiceResponse::getCreatedDateTime,
                        ReadInvoiceResponse::getPaymentDate,
                        ReadInvoiceResponse::getBuyer,
                        ReadInvoiceResponse::getSeller,
                        ReadInvoiceResponse::getStatus)
                .containsExactly(expectedInvoiceId,
                        expectedInvoiceVersion,
                        expectedInvoiceCreateDateTime,
                        expectedInvoicePaymentDate,
                        expectedInvoiceBuyerName,
                        expectedInvoiceSellerName,
                        expectedInvoiceStatus);

    }
}