package gyc.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfigUpdateRequest(@NotBlank String key, @NotBlank String value) {
}
