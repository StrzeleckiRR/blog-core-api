package pl.mojastrona.util;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.mojastrona.post.test.helper.UserCreator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class BaseServiceIT {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private DBCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

}
