package pl.mojastrona.invoice.detail;

import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class UpdateInvoiceDetailRequest {

    @NotNull
    @NotBlank
    @Size(max = 100)
    String productName;

    @NotNull
    BigDecimal price;

    @Version
    @NotNull
    Integer version;
}
