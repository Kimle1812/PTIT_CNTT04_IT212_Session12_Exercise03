package org.example.session12.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CitizenIdValidator implements ConstraintValidator<CitizenId, String> {

    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");

    @Override
    public void initialize(CitizenId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        if (!CCCD_PATTERN.matcher(value).matches()) {
            return false;
        }

        String provinceCode = value.substring(0, 3);
        int provinceValue = Integer.parseInt(provinceCode);
        if (provinceValue < 1 || provinceValue > 96) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Mã tỉnh/thành phố (3 số đầu) trên CCCD không hợp lệ")
                   .addConstraintViolation();
            return false;
        }

        char genderCenturyChar = value.charAt(3);
        if (genderCenturyChar < '0' || genderCenturyChar > '9') {
            return false;
        }

        return true;
    }
}
