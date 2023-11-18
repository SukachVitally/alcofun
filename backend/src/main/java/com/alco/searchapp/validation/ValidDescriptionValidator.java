package com.alco.searchapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidDescriptionValidator implements ConstraintValidator<ValidDescription, String> {
    private static final List<String> BLACK_LIST = List.of(
            "alcomag", "rozetka", "okwine", "інтернет-магазин", "АЛКОМАГ", "розетка"
    );

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return BLACK_LIST.stream().noneMatch(value -> s.toLowerCase().contains(value.toLowerCase()));
    }
}
