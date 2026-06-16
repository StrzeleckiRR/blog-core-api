package pl.mojastrona.invoice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;
import pl.mojastrona.invoice.helper.InvoiceCreator;
import pl.mojastrona.util.BaseIT;
import pl.mojastrona.util.BaseServiceIT;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceServiceIT extends BaseServiceIT {

    @Autowired
    private InvoiceCreator invoiceCreator;

    @Autowired
    private InvoiceService underTest;

    @Test
    void givenInvoices_whenGetFind_thenCorrectResponse() {
        //given

        String sellerContaining = "Sel";
        String buyerContaining = "arc";
        int page = 0;
        int size = 3;

        Invoice publishedAndActive1 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(6)));
        Invoice publishedAndActive2 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(5)));
        Invoice publishedAndActive3 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(4)));

        //not matching by deleted
        invoiceCreator.createInvoice(invoice -> invoice.setStatus(InvoiceStatus.DELETED));

        Invoice publishedAndActive4 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(3)));

        // not matching by seller
        invoiceCreator.createInvoice(invoice -> invoice.setSeller("nie pasuje sprzedawca"));

        Invoice publishedAndActive5 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now().minusDays(2)));

        Invoice publishedAndActive6 = invoiceCreator.createInvoice(invoice -> invoice.setPaymentDate(LocalDate.now()));

        Page<FindInvoiceResponse> resultPage = underTest.find(sellerContaining, buyerContaining, page, size);
        //then

        Assertions.assertThat(resultPage).isNotNull();
        Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(6);
        Assertions.assertThat(resultPage.getContent()).hasSize(3)
                .extracting(FindInvoiceResponse::id).containsExactly(
                        publishedAndActive1.getId(),
                        publishedAndActive2.getId(),
                        publishedAndActive3.getId()
                );

    }
}
