package pl.mojastrona.invoice.detail;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.invoice.Invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
@ToString(exclude = {"invoice"})
@EqualsAndHashCode(exclude = {"invoice"})
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @NotNull
    private Integer version;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String productName;

    @NotNull
    private BigDecimal price;

    @CreatedDate
    @NotNull
    private LocalDateTime createdDate;

    @LastModifiedDate
    @NotNull
    private LocalDateTime lastModifiedDateTime;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Invoice invoice;

//    public InvoiceDetail() {
//    }
//
//    public InvoiceDetail(String productName, BigDecimal price, Invoice invoice) {
//
//        this.productName = productName;
//        this.price = price;
//        this.invoice = invoice;
//    }
//
//    public InvoiceDetail(Long id, Integer version, String productName, BigDecimal price, LocalDateTime createdDate, LocalDateTime lastModifiedDateTime, Invoice invoice) {
//        this.id = id;
//        this.version = version;
//        this.productName = productName;
//        this.price = price;
//        this.createdDate = createdDate;
//        this.lastModifiedDateTime = lastModifiedDateTime;
//        this.invoice = invoice;
//    }


}
