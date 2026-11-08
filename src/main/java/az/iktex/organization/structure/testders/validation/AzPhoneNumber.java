package az.iktex.organization.structure.testders.validation;

import az.iktex.organization.structure.testders.enums.ValidationMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD, PARAMETER, METHOD, ANNOTATION_TYPE, RECORD_COMPONENT})
@Retention(RUNTIME)
@Constraint(validatedBy = AzPhoneNumberValidator.class)
public @interface AzPhoneNumber {

    String message() default ValidationMessage.Keys.AZ_PHONE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}