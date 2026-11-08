package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String name,

        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String surname,

        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Email(message = ValidationMessage.Keys.EMAIL)
        @Size(max = 100, message = ValidationMessage.Keys.SIZE_MAX)
        String email,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        Gender gender
) {
}
