package gyc.admin.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * The wire shape shared with the Consumer service. This class is duplicated
 * (not imported) on the other side of the bridge — the JSON shape is the
 * only real contract between the two repositories. Keep it in sync with
 * {@code consumer-backend}'s copy and bump {@code CONTRACT_VERSION} on any
 * breaking change to a payload.
 */
public record EventEnvelope(
        String eventId,
        String eventType,
        Instant occurredAt,
        String sourceService,
        Map<String, Object> payload) {

    public static final int CONTRACT_VERSION = 1;

    public static EventEnvelope of(String eventType, String sourceService, Map<String, Object> payload) {
        return new EventEnvelope(UUID.randomUUID().toString(), eventType, Instant.now(), sourceService, payload);
    }
}
