package pl.robloj.example.app.dto.validators;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.dto.EmployeeRole;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeValidatorTest {

    @Test
    void givenValidationFactory_whenEmployeeIsValid_returnOK(){

        var employee = Employee.builder()
                .age(43)
                .email("valid@email.com")
                .pesel("83092629182")
                .firstName("Robert")
                .lastName("Smith")
                .accountNumber("38105012301000009316735621")
                .linkedInProfile("https://linkedin.com/profiles/rsmith")
                .role(EmployeeRole.COMPLIANCE)
                .build();

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var result = validator.validate(employee);
            assertEquals(result.size(), 0);
        }
    }

    @Test
    void givenValidationFactory_whenEmployeeHasInvalidEmail_returnValidationConstraint(){

        var invalidEmail = "INVALID_EMAIL";
        var employee = Employee.builder()
                .age(43)
                .email(invalidEmail)
                .pesel("83092629182")
                .firstName("Robert")
                .lastName("Smith")
                .accountNumber("91109024023639359633743839")
                .linkedInProfile("https://linkedin.com/profiles/rsmith")
                .role(EmployeeRole.COMPLIANCE)
                .build();

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var result = validator.validate(employee);
            assertEquals(result.size(), 1);
            var constraint = result.stream().findFirst();
            assertTrue(constraint.isPresent());
            assertEquals(constraint.get().getInvalidValue(), invalidEmail);
        }
    }
}
