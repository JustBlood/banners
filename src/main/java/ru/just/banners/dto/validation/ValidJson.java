package ru.just.banners.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Constraint(validatedBy= JsonConstraintValidator.class)
@Target({METHOD, FIELD, TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJson {
    String message() default "{ru.just.banners.dto.validation.JsonConstraintValidator.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
