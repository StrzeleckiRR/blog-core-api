package pl.mojastrona.invoice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateInvoiceRequest(@NotBlank @Size(max = 150) String buyer,
                                   @NotBlank @Size(max = 150) String seller, @NotNull LocalDate paymentDate,
                                   @NotNull Integer version) {

    public UpdateInvoiceRequest(String buyer, String seller, LocalDate paymentDate, Integer version) {
        this.buyer = buyer;
        this.seller = seller;
        this.paymentDate = paymentDate;
        this.version = version;
    }

    @Override
    public Integer version() {
        return version;
    }

    @Override
    public String buyer() {
        return buyer;
    }

    @Override
    public String seller() {
        return seller;
    }
}
