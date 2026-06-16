package pl.mojastrona.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.mojastrona.comment.Comment;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
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
    @Size(max = 5000)
    private String text;


    @NotBlank
    @NotNull
    @Size(max = 100)
    @NotAudited
    private String author;

    @FutureOrPresent
    private LocalDateTime publicationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostScope scope;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    public Post(Post old){

        this.id = old.id;
        this.version = old.version;
        this.createdDateTime = old.createdDateTime;
        this.lastModifiedDateTime = old.lastModifiedDateTime;
        this.text = old.text;
        this.author = old.author;
        this.scope = old.scope;
        this.publicationDate = old.publicationDate;
        this.status = old.status;
        this.user = old.user;
        this.comments = old.comments;
    }

    public Post( String text,  String author, PostScope scope, LocalDateTime publicationDate, User user) {

        this.text = text;
        this.author = author;
        this.scope = scope;
        this.publicationDate = publicationDate;
        this.status = PostStatus.ACTIVE;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public PostScope getScope() {
        return scope;
    }

    public void setScope(PostScope scope) {
        this.scope = scope;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAuthor(Long userId){
        return user.getId().equals(userId);
    }
}
