package pl.robloj.example.app.dto.validators;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IBANValidatorTest {

    @Test
    void givenValidAccountNumber_whenValidate_thenOK() {
        var accountNumber = "38105012301000009316735621";
        var validator = new IBANValidator();
        var result = validator.isValid(accountNumber, null);
        assertTrue(result);
    }

    @Test
    void givenInvalidAccountNumber_whenValidate_thenFalse() {
        var accountNumber = "98105012301000009316735629";
        var validator = new IBANValidator();
        var result = validator.isValid(accountNumber, null);
        assertFalse(result);
    }

    @Test
    void givenWrongLengthAccountNumber_whenValidate_thenFalse() {

        var accountNumberTooShort = "3810501230100000931673562";
        var accountNumberTooLong = "381050123010000093167356219";

        var validator = new IBANValidator();

        for (var accountNumber: List.of(accountNumberTooShort, accountNumberTooLong)) {
            var result = validator.isValid(accountNumber, null);
            assertFalse(result);
        }
    }

    @Test
    void givenValidationFactory_whenAccountsValid_returnOK(){

        var employee = ExampleDto.builder()
                .optionalAccountNumber("91109024023639359633743839")
                .requiredAccountNumber("43109024029724757444534262")
                .build();

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var result = validator.validate(employee);
            assertEquals(result.size(), 0);
        }
    }

    @Test
    void givenValidationFactory_whenOptionalAccountIsEmpty_returnOK(){

        var employee = ExampleDto.builder()
                .optionalAccountNumber("")
                .requiredAccountNumber("43109024029724757444534262")
                .build();

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var result = validator.validate(employee);
            assertEquals(result.size(), 0);
        }
    }

    @Test
    void givenValidationFactory_whenRequiredAccountIsEmpty_returnValidationConstraint(){

        var employee = ExampleDto.builder()
                .optionalAccountNumber("91109024023639359633743839")
                .requiredAccountNumber("")
                .build();

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var result = validator.validate(employee);
            assertEquals(result.size(), 1);
            var constraint = result.stream().findFirst();
            assertTrue(constraint.isPresent());

            var invalidFieldName = constraint.get().getPropertyPath().iterator().next().getName();
            assertEquals(invalidFieldName, "requiredAccountNumber");
        }
    }
}
