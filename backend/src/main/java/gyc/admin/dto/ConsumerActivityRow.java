package gyc.admin.dto;

import java.time.Instant;

import gyc.admin.domain.entity.ConsumerActivityView;

public record ConsumerActivityRow(
        Long id, String requestId, String userId, String kind, String status, String detail, Instant recordedAt) {

    public static ConsumerActivityRow from(ConsumerActivityView view) {
        return new ConsumerActivityRow(
                view.getId(), view.getRequestId(), view.getUserId(), view.getKind(),
                view.getStatus(), view.getDetail(), view.getRecordedAt());
    }
}
