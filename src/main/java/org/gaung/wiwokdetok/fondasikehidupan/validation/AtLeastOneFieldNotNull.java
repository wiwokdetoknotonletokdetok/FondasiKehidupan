package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AtLeastOneFieldNotNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneFieldNotNull {

    String message() default "At least one field not null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}