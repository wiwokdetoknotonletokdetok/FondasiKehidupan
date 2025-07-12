package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneFieldNotNullValidator implements ConstraintValidator<AtLeastOneFieldNotNull, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value != null) {
                    return true;
                }
            } catch (IllegalAccessException ignored) {
            }
        }

        return false;
    }
}
