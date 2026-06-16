package pl.mojastrona.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.invoice.detail.InvoiceDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @NotNull
    private Integer version;

    @CreatedDate
    @NotNull
    private LocalDateTime createdDate;

    @LastModifiedDate
    @NotNull
    private LocalDateTime lastModifiedDateTime;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    @NotBlank
    @Size(max = 150)
    private String buyer;


    @NotNull
    @NotBlank
    @Size(max = 150)
    @NotAudited
    private String seller;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Set<InvoiceDetail> details;

    public Invoice(Invoice old) {

        this.id = old.id;
        this.version = old.version;
        this.createdDate = old.createdDate;
        this.lastModifiedDateTime = old.lastModifiedDateTime;
        this.paymentDate = old.paymentDate;
        this.buyer = old.buyer;
        this.seller = old.seller;
        this.status = old.status;
    }

    public Invoice(LocalDate paymentDate, String buyer, String seller) {

        this.paymentDate = paymentDate;
        this.buyer = buyer;
        this.seller = seller;
        this.status = InvoiceStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getSeller() {
        return seller;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public Set<InvoiceDetail> getDetails() {
        return details;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void setDetails(Set<InvoiceDetail> details) {
        this.details = details;
    }
}
