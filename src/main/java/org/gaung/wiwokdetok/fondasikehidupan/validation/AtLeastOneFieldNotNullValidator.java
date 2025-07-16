package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class AtLeastOneFieldNotNullValidator implements ConstraintValidator<AtLeastOneFieldNotNull, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null) {
                    Object value = pd.getReadMethod().invoke(object);
                    if (value != null) {
                        return true;
                    }
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            return false;
        }

        return false;
    }
}
