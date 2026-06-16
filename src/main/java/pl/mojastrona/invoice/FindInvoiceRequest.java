package pl.mojastrona.invoice;

import java.time.LocalDate;
import java.util.Set;

public record FindInvoiceRequest(LocalDate paymentDateMin, LocalDate paymentDateMax, String seller, Set<InvoiceStatus> invoiceStatuses) {
}
