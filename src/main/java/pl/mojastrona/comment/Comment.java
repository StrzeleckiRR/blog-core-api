package pl.mojastrona.comment;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.post.Post;
import pl.mojastrona.user.User;


import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Data
@NoArgsConstructor
@ToString(exclude = {"post", "user"})
@EqualsAndHashCode(exclude = {"post", "user"})
@Builder(toBuilder = true)
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @NotNull
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @NotNull
    private LocalDateTime lastModifiedDateTime;

    @NotBlank
    @NotNull
    @Size(max = 5000)
    private String text;


    @NotBlank
    @NotNull
    @Size(max = 100)
    @NotAudited
    private String author;

    @ManyToOne(optional = false, fetch = FetchType.LAZY )
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    public boolean isAuthor(Long userId){
        return user.getId().equals(userId);
    }

}
