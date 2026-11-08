package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record BulkEnrollmentRequest(
        @NotEmpty(message = ValidationMessage.Keys.REQUIRED)
        List<@NotNull(message = ValidationMessage.Keys.REQUIRED) @Positive(message = ValidationMessage.Keys.POSITIVE) Long> courseIds
) {
}
