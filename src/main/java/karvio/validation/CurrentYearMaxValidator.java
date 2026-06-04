package karvio.validation;

import karvio.annotation.MaxCurrentYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CurrentYearMaxValidator implements ConstraintValidator<MaxCurrentYear, Integer> {

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) return true;
        return year <= LocalDate.now().getYear();
    }
}