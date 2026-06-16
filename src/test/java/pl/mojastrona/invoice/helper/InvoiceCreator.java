package pl.mojastrona.invoice.helper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.invoice.Invoice;
import pl.mojastrona.invoice.InvoiceStatus;
import pl.mojastrona.post.Post;

import java.time.LocalDate;
import java.util.function.Consumer;

@Component
public class InvoiceCreator {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DetailsCreator detailsCreator;


    private Invoice buildDefaultInvoice() {
        return Invoice.builder()
                .version(0)
                .paymentDate(LocalDate.now())
                .buyer("Marcin Strzelecki")
                .seller("Seller1")
                .status(InvoiceStatus.ACTIVE)
                .build();
    }


    @Transactional
    public Invoice createInvoice() {

        Invoice invoice = buildDefaultInvoice();
        entityManager.persist(invoice);
        return invoice;
    }

    @Transactional
    public Invoice createInvoice(Consumer<Invoice> modifier) {

        Invoice invoice = buildDefaultInvoice();
        modifier.accept(invoice);
        entityManager.persist(invoice);
        return invoice;
    }

    @Transactional
    public Invoice createInvoiceWithDetails() {

        Invoice invoice = createInvoice();
        detailsCreator.createDetails(invoice);
        return invoice;
    }
}
