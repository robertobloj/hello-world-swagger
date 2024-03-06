package pl.robloj.example.app.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigInteger;

public class IBANValidator implements ConstraintValidator<IBAN, String> {

    public static final int EXPECTED_LENGTH = 26;
    public static final int POLAND_IBAN_CODE = 2521;

    private boolean allowEmpty;

    @Override
    public void initialize(IBAN constraintAnnotation) {
        allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (allowEmpty && value.isEmpty()) {
            return true;
        }
        else if (!checkLength(value)) {
            return false;
        }

        var controlSum = value.substring(0, 2);
        var accountNumber = value.substring(2);
        var accountNumberToVerification = new BigInteger(accountNumber + IBANValidator.POLAND_IBAN_CODE + "00");
        var modulo = accountNumberToVerification.mod(BigInteger.valueOf(97));
        var calculatedControlSum = 98 - modulo.intValue();
        var calculatedControlSumStr = String.format("%02d", calculatedControlSum);

        return controlSum.equals(calculatedControlSumStr);
    }

    private boolean checkLength(CharSequence value) {
        return !value.isEmpty() && value.length() == IBANValidator.EXPECTED_LENGTH;
    }
}
