package pl.mojastrona.invoice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.mojastrona.invoice.helper.InvoiceCreator;
import pl.mojastrona.util.BaseServiceIT;

import java.time.LocalDate;
import java.util.Set;

class InvoiceRepositoryIT extends BaseServiceIT {

    @Autowired
    private InvoiceCreator invoiceCreator;

    @Autowired
    private InvoiceRepository underTest;

    @Test
    void givenInvoices_whenFindByBuyerContainingAndSellerContainingAndStatusInOrderByPaymentDate_thenCorrectResponse() { //Marcin Strzelecki
        //given

        String sellerContaining = "Sel";
        String buyerContaining = "arc";
        int page = 0;
        int size = 3;
        Set<InvoiceStatus> invoiceStatuses = Set.of(InvoiceStatus.ACTIVE, InvoiceStatus.DRAFT);

        Invoice publishedAndActive1 = invoiceCreator.createInvoice(invoice ->{
                invoice.setSeller("Seller2");
                invoice.setBuyer("Marcin");
                invoice.setPaymentDate(LocalDate.now().minusDays(6));
        });
        Invoice publishedAndActive2 = invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("Seller1");
            invoice.setBuyer("Marcin");
            invoice.setPaymentDate(LocalDate.now().minusDays(5));
        });
        Invoice publishedAndActive3 = invoiceCreator.createInvoice(invoice -> {
                    invoice.setSeller("Seller2");
                    invoice.setBuyer("Marcin");
                    invoice.setPaymentDate(LocalDate.now().minusDays(4));
                });

        Invoice publishedAndActive4 = invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("Seller1");
            invoice.setBuyer("Marcin");
            invoice.setPaymentDate(LocalDate.now().minusDays(3));
        });

        Invoice publishedAndActive5 = invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("Seller2");
            invoice.setBuyer("Marcin");
            invoice.setPaymentDate(LocalDate.now().minusDays(2));
        });

        Invoice publishedAndActive6 = invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("Seller2");
            invoice.setBuyer("Marcin");
            invoice.setPaymentDate(LocalDate.now());
        });

        //not matching status
        invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("Seller2");
            invoice.setBuyer("Marcin");
            invoice.setStatus(InvoiceStatus.DELETED);
        });

        // not matching by seller
        invoiceCreator.createInvoice(invoice -> {
            invoice.setSeller("nie pasuje");
            invoice.setBuyer("Marcin");
        });


        Page<Invoice> resultPage = underTest.findByBuyerContainingAndSellerContainingAndStatusInOrderByPaymentDate(buyerContaining, sellerContaining, invoiceStatuses, PageRequest.of(page, size));
        //then

        Assertions.assertThat(resultPage).isNotNull();
        Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(6);
        Assertions.assertThat(resultPage.getContent()).hasSize(3)
                .extracting(Invoice::getId).containsExactly(
                        publishedAndActive1.getId(),
                        publishedAndActive2.getId(),
                        publishedAndActive3.getId()
                );

    }
}
