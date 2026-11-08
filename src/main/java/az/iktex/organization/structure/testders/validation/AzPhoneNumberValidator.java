package az.iktex.organization.structure.testders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class AzPhoneNumberValidator
        implements ConstraintValidator<AzPhoneNumber, String> {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\+994(50|51|55|70|77|99)[0-9]{7}");

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null) {
            return true;
        }

        String phone = value.replaceAll("\\s+", "");
        return PHONE_PATTERN.matcher(phone).matches();
    }
}