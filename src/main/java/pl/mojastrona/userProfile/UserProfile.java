package pl.mojastrona.userProfile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.user.User;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Table(name = "user_profile")
public class UserProfile {

    @Id
    private Long id; // zastosowanie @MapsId - StrzeleckiRR

    @NotBlank()
    @Size(max = 100, message = "Imię może mieć maksymalnie 100 znaków")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotBlank()
    @Size(max = 100, message = "Nazwisko może mieć maksymalnie 100 znaków")
    @Column(name = "surname", length = 100, nullable = false)
    private String surname;

    @NotBlank()
    @Size(max = 100, message = "Email może mieć maksymalnie 100 znaków")
    @Pattern(
            regexp = "^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$",
            message = "Wprowadź poprawny adres email (musi zawierać @ oraz poprawną końcówkę domeny)"
    )
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Size(max = 20, message = "Numer telefonu może mieć maksymalnie 20 znaków")
    @Column(name = "phone", length = 20)
    private String phone;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
