package pl.mojastrona.userSettings;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
@NoArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserSettings {

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

    @NotNull
    private Boolean showPanel1;

    @NotNull
    private Boolean darkMode;

    @OneToOne(optional = false , fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
