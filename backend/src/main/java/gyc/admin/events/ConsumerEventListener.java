package gyc.admin.events;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyc.admin.domain.entity.ConsumerActivityView;
import gyc.admin.repository.ConsumerActivityViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code consumer.events} and folds facts into the local, read-only
 * {@link ConsumerActivityView} mirror. Never calls the Consumer backend or
 * touches its database directly — this is the "consume" arrow in the
 * diagram, applied asynchronously and at Admin's own pace.
 */
@Component
public class ConsumerEventListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ConsumerEventListener.class);

    private final ObjectMapper objectMapper;
    private final ConsumerActivityViewRepository repository;

    public ConsumerEventListener(ObjectMapper objectMapper, ConsumerActivityViewRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message.getBody(), EventEnvelope.class);
            apply(envelope);
        } catch (Exception e) {
            log.error("Failed to process consumer.events message, dropping it", e);
        }
    }

    // Package-private so unit tests can exercise the fold logic directly,
    // without constructing a real Redis Message.
    void apply(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.payload();
        ConsumerEventType type = ConsumerEventType.valueOf(envelope.eventType());

        String requestId = String.valueOf(payload.get("requestId"));
        String userId = String.valueOf(payload.get("userId"));
        // SESSION_FLAGGED carries no "kind" per the contract — default to
        // blank rather than stringifying null into the literal text "null".
        String kind = payload.containsKey("kind") ? String.valueOf(payload.get("kind")) : "";

        String status = switch (type) {
            case REQUEST_SUBMITTED -> "SUBMITTED";
            case ACTION_COMPLETED -> "COMPLETED";
            case SESSION_FLAGGED -> "FLAGGED";
        };
        String detail = switch (type) {
            case REQUEST_SUBMITTED -> "";
            case ACTION_COMPLETED -> String.valueOf(payload.get("result"));
            case SESSION_FLAGGED -> String.valueOf(payload.get("reason"));
        };

        repository.save(new ConsumerActivityView(null, requestId, userId, kind, status, detail, Instant.now()));
    }
}
