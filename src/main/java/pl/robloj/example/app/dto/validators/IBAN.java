package pl.robloj.example.app.dto.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings("unused")
@Documented
@Constraint(validatedBy = { IBANValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface IBAN {
    String message() default "Polish IBAN is not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    boolean allowEmpty() default false;

}
