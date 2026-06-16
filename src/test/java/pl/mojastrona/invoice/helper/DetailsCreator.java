package pl.mojastrona.invoice.helper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.invoice.Invoice;
import pl.mojastrona.invoice.detail.InvoiceDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DetailsCreator {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public InvoiceDetail createDetails(Invoice invoice) {

        InvoiceDetail detail = InvoiceDetail.builder()
                .version(0)
                .productName("product" )
                .price(BigDecimal.valueOf(230.00))
                .invoice(invoice)
                .build();

        entityManager.persist(detail);
        return detail;
    }

    @Transactional
    public InvoiceDetail createDetails(Invoice invoice, int number) {

        InvoiceDetail detail = InvoiceDetail.builder()
                .version(0)
                .productName("product " + number)
                .price(BigDecimal.valueOf(230.00))
                .createdDate(LocalDateTime.now().minusMinutes(number))
                .invoice(invoice)
                .build();

        entityManager.persist(detail);
        return detail;
    }
}
