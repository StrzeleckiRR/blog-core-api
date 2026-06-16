package pl.mojastrona.invoice.detail;

import lombok.Value;
import pl.mojastrona.invoice.ReadInvoiceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ReadInvoiceDetailResponse {


    Long id;

    Integer version;

    String productName;

    LocalDateTime createdDate;

    BigDecimal price;

    ReadInvoiceResponse invoice;



    public static ReadInvoiceDetailResponse from(InvoiceDetail invoiceDetail) {
        return new ReadInvoiceDetailResponse(
                invoiceDetail.getId(),
                invoiceDetail.getVersion(),
                invoiceDetail.getProductName(),
                invoiceDetail.getCreatedDate(),
                invoiceDetail.getPrice(),
                ReadInvoiceResponse.from(invoiceDetail.getInvoice()));
    }
}
