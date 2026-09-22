package gyc.admin.domain.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A record owned exclusively by Admin. Never written by the Consumer service. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private int revision;

    private Instant createdAt;

    private Instant updatedAt;

    public RecordEntity(String title, String body) {
        this.title = title;
        this.body = body;
        this.revision = 1;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void applyEdit(String title, String body) {
        this.title = title;
        this.body = body;
        this.revision += 1;
        this.updatedAt = Instant.now();
    }
}
