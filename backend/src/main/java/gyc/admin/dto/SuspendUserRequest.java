package gyc.admin.dto;

import jakarta.validation.constraints.NotNull;

public record SuspendUserRequest(@NotNull Boolean suspended, String reason) {
}
