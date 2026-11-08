package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PassportRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 20, message = ValidationMessage.Keys.SIZE_MAX)
        @Pattern(regexp = "[A-Za-z0-9-]+", message = ValidationMessage.Keys.SERIAL_NUMBER_PATTERN)
        String serialNumber,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @PastOrPresent(message = ValidationMessage.Keys.PAST_OR_PRESENT)
        LocalDate issueDate,

        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String country
) {
}
