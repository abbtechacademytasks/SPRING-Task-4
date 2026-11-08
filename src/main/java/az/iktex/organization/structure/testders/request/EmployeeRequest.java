package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String name,

        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 50, message = ValidationMessage.Keys.SIZE_MAX)
        String surname,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @Positive(message = ValidationMessage.Keys.POSITIVE)
        @Digits(integer = 8, fraction = 2, message = ValidationMessage.Keys.DIGITS)
        BigDecimal salary,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @Positive(message = ValidationMessage.Keys.POSITIVE)
        Long departmentId
) {
}
