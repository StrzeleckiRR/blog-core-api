package pl.mojastrona.invoice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record CreateInvoiceRequest(@NotBlank @Size(max = 150) String buyer,
                                   @NotBlank @Size(max = 150) String seller,
                                   @NotNull LocalDate paymentDate) {

    public CreateInvoiceRequest(String buyer, String seller, LocalDate paymentDate) {
        this.buyer = buyer;
        this.seller = seller;
        this.paymentDate = paymentDate;
    }

    @Override
    public String buyer() {
        return buyer;
    }

    @Override
    public String seller() {
        return seller;
    }

    @Override
    public LocalDate paymentDate() {
        return paymentDate;
    }
}
