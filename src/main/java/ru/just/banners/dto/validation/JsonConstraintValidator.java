package ru.just.banners.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonConstraintValidator implements ConstraintValidator<ValidJson, String>{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        try {
            new JSONObject(value);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
