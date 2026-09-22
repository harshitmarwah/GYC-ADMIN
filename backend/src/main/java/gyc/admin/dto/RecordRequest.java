package gyc.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record RecordRequest(@NotBlank String title, @NotBlank String body) {
}
