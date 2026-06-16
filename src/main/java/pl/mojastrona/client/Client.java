package pl.mojastrona.client;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.accountant.Accountant;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
@NoArgsConstructor
@ToString(exclude = {"accountants"})
@EqualsAndHashCode(exclude = {"accountants"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @NotNull
    private Integer version;

    @CreatedDate
    @NotNull
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @NotNull
    private LocalDateTime lastModifiedDateTime;

    @NotBlank
    @NotNull
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "clients")
    private Set<Accountant> accountants;
}
