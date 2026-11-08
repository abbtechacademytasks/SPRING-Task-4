package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BookRequest(
        @NotBlank(message = ValidationMessage.Keys.REQUIRED)
        @Size(max = 150, message = ValidationMessage.Keys.SIZE_MAX)
        String title,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @PositiveOrZero(message = ValidationMessage.Keys.NOT_NEGATIVE)
        @Digits(integer = 6, fraction = 2, message = ValidationMessage.Keys.DIGITS)
        BigDecimal price,

        @NotNull(message = ValidationMessage.Keys.REQUIRED)
        @Positive(message = ValidationMessage.Keys.POSITIVE)
        Integer pageCount
) {
}
