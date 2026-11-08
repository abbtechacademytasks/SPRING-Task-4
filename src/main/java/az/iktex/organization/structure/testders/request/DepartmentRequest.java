package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import az.iktex.organization.structure.testders.validation.AzPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String name,

        @Size(max = 100, message = ValidationMessage.Keys.SIZE_MAX)
        String location,

        @AzPhoneNumber
        String phone
) {
}