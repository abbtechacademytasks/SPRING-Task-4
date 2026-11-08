package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String name,

        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String surname,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @PositiveOrZero(message = ValidationMessage.Keys.NOT_NEGATIVE)
        Integer age
) {
}
