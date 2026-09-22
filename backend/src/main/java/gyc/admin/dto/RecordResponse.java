package gyc.admin.dto;

import java.time.Instant;

import gyc.admin.domain.entity.RecordEntity;

public record RecordResponse(Long id, String title, String body, int revision, Instant updatedAt) {
    public static RecordResponse from(RecordEntity entity) {
        return new RecordResponse(entity.getId(), entity.getTitle(), entity.getBody(), entity.getRevision(), entity.getUpdatedAt());
    }
}
