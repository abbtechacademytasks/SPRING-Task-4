package az.iktex.organization.structure.testders.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Common API error response")
public record ErrorResponse(
        @Schema(description = "HTTP status", example = "404")
        int status,

        @Schema(description = "Application error code", example = "ERR-2000")
        String code,

        @Schema(description = "Localized error message", example = "Student not found: 99999")
        String message,

        @Schema(description = "Validation errors: field name → message", example = "{}")
        Map<String, String> errors,

        @Schema(description = "Request trace ID", example = "study-request-123")
        String traceId,

        @Schema(description = "Error timestamp", example = "2026-11-09T18:30:00")
        LocalDateTime timestamp
) {
}
