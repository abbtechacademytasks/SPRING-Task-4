package az.iktex.organization.structure.testders.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String code,
        String message,
        Map<String, String> errors,
        String traceId,
        LocalDateTime timestamp
) {
}