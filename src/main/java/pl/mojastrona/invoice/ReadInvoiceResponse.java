package pl.mojastrona.invoice;

import lombok.Value;
import pl.mojastrona.invoice.detail.InvoiceDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Value
public class ReadInvoiceResponse {
    Long id;
    Integer version;
    LocalDateTime createdDateTime;
    LocalDate paymentDate;
    String buyer;
    String seller;
    InvoiceStatus status;
    List<DetailResponse> detailResponses;

    public static ReadInvoiceResponse from(Invoice invoice) {
        return new ReadInvoiceResponse(
                invoice.getId(),
                invoice.getVersion(),
                invoice.getCreatedDate(),
                invoice.getPaymentDate(),
                invoice.getBuyer(),
                invoice.getSeller(),
                invoice.getStatus(),
                invoice.getDetails().stream()
                        .map(DetailResponse::from)
                        .sorted(Comparator.comparing(DetailResponse::getCreatedDate).reversed())
                        .collect(Collectors.toList())

        );
    }

    @Value
    public static class DetailResponse {


        Long id;

        Integer version;

        String productName;

        LocalDateTime createdDate;

        BigDecimal price;



        public static DetailResponse from(InvoiceDetail invoiceDetail) {
            return new DetailResponse(
                    invoiceDetail.getId(),
                    invoiceDetail.getVersion(),
                    invoiceDetail.getProductName(),
                    invoiceDetail.getCreatedDate(),
                    invoiceDetail.getPrice());
        }
    }


}
