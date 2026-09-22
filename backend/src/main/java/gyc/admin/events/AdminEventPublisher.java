package gyc.admin.events;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminEventPublisher {

    public static final String CHANNEL = "admin.events";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminEventPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(AdminEventType type, Map<String, Object> payload) {
        try {
            EventEnvelope envelope = EventEnvelope.of(type.name(), "admin-backend", payload);
            redisTemplate.convertAndSend(CHANNEL, objectMapper.writeValueAsString(envelope));
        } catch (Exception e) {
            // A first skeleton: publish failures are logged, not retried or
            // dead-lettered. See ARCHITECTURE notes on "not built yet".
            throw new IllegalStateException("Failed to publish " + type + " to " + CHANNEL, e);
        }
    }
}
