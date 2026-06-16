package pl.mojastrona.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.post.test.helper.UserCreator;
import pl.mojastrona.user.User;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private DBCleaner dbCleaner;

    @Autowired
    protected UserCreator userCreator;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        //userCreator.createUser();
    }

    protected ResultActions performPost(String url,Object request) throws Exception {
        String content = objectMapper.writeValueAsString(request);

        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

    }

    protected ResultActions performPut(String url,Long id, Object request) throws Exception {
        String content = objectMapper.writeValueAsString(request);

        return mockMvc.perform(put(url,id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

    }

    protected ResultActions performGet(String url, Long id) throws Exception {

        return mockMvc.perform(get(url,id));

    }

    protected ResultActions performGet(String url, Map<String, String> params) throws Exception {

        MockHttpServletRequestBuilder requestBuilder = get(url);
        params.forEach(requestBuilder::param);
        return mockMvc.perform(requestBuilder);

    }

    protected LocalDateTime parseDateTime(String json, String jsonPath) {
        return LocalDateTime.parse(
                JsonPath.compile(jsonPath).read(json)
        );
    }

    @Transactional
    protected User createUserAndAuthenticate() {
        User user = userCreator.createUser();
        createSecurityContext(user);
        return user;
    }

    @Transactional
    protected User createAdminAndAuthenticate() {
        User user = userCreator.createAdmin();
        createSecurityContext(user);
        return user;
    }

    private static void createSecurityContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );
    }
}
