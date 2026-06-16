package pl.mojastrona.invoice;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;
import pl.mojastrona.BaseUnitTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceServiceTest extends BaseUnitTest {

    @InjectMocks
    private InvoiceService underTest;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Test
    void givenNoResults_whenFindAll_thenReturnEmptyPage() {

        int expectedInvoiceSize = 5;

        FindInvoiceRequest request = new FindInvoiceRequest(null,null,null,null);

        Pageable pageable = Pageable.ofSize(expectedInvoiceSize);

        Mockito.when(invoiceRepository.findAll(Mockito.any(), eq(pageable))).thenReturn(Page.empty(pageable));

        Page<FindInvoiceResponse> response = underTest.find(request, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getSize()).isEqualTo(expectedInvoiceSize);

    }

    @Test
    void givenTwoResults_whenFindAll_thenReturnResponseInCorrectOrder() {

        FindInvoiceRequest request = new FindInvoiceRequest(LocalDate.of(2026,5,15),LocalDate.of(2026,6,15),"seller1"
                ,Set.of(InvoiceStatus.ACTIVE));

        int expectedPageSize = 10;

        long invoice1Id = 1L;
        long invoice2Id = 2L;

        Pageable pageable = Pageable.ofSize(expectedPageSize);

        Invoice invoice1 = Invoice.builder()
                .id(invoice1Id)
                .details(Set.of())
                .build();

        Invoice invoice2 = Invoice.builder()
                .id(invoice2Id)
                .details(Set.of())
                .build();

        List<Invoice> invoiceList = List.of(invoice1, invoice2);

        Mockito.when(invoiceRepository.findAll(Mockito.any(), eq(pageable))).thenReturn(
                new PageImpl<>(
                        invoiceList, pageable, invoiceList.size())
        );
        Page<FindInvoiceResponse> response = underTest.find(request, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent()).
                extracting(FindInvoiceResponse::id)
                .containsExactly(invoice1Id,invoice2Id);
        assertThat(response.getSize()).isEqualTo(expectedPageSize);


    }
}