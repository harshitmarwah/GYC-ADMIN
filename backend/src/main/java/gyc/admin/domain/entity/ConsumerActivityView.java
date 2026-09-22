package gyc.admin.domain.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A read-only local mirror populated ONLY by {@link gyc.admin.events.ConsumerEventListener}.
 * Proves the reverse fold in the diagram: Consumer -> consumer.events -> Admin.
 * Nothing in the Admin API writes here directly.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerActivityView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;

    private String userId;

    private String kind;

    private String status; // SUBMITTED | COMPLETED | FLAGGED

    private String detail; // result summary, or the flag reason

    private Instant recordedAt;
}
