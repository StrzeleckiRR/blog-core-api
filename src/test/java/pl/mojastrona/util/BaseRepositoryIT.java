package pl.mojastrona.util;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.mojastrona.post.test.helper.CommentCreator;
import pl.mojastrona.post.test.helper.PostCreator;

@ActiveProfiles("test")
@DataJpaTest
@Import({DBCleaner.class, PostCreator.class, CommentCreator.class})
public class BaseRepositoryIT {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private DBCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

}
