package gyc.admin;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyc.admin.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the real controller -> service -> repository -> SQLite stack
 * through HTTP, behind the real JWT security filter chain. Redis is mocked
 * out — that side of the bridge is unit-tested separately (see
 * ConsumerEventListenerTest and RecordServiceTest) and doesn't need a live
 * broker for this test to prove the REST + persistence layers work together.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminApiIntegrationTest {

    @DynamicPropertySource
    static void useAnIsolatedSqliteFilePerTestRun(DynamicPropertyRegistry registry) {
        String path = System.getProperty("java.io.tmpdir") + "/admin-integration-test-" + UUID.randomUUID() + ".db";
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:" + path);
    }

    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/records")).andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginThenCreateThenListRoundTrips() throws Exception {
        String token = login();

        mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"title\":\"Integration record\",\"body\":\"created via MockMvc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration record"))
                .andExpect(jsonPath("$.revision").value(1));

        mockMvc.perform(get("/api/records").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Integration record"));
    }

    @Test
    void suspendUserIsAcceptedAndPublishesToRedis() throws Exception {
        String token = login();

        mockMvc.perform(post("/api/users/alice/suspend")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"suspended\":true,\"reason\":\"testing\"}"))
                .andExpect(status().isAccepted());

        verify(stringRedisTemplate).convertAndSend(eq("admin.events"), anyString());
    }

    private String login() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, LoginResponse.class).token();
    }
}
