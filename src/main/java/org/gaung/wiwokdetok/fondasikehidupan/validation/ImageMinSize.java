package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ImageMinSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageMinSize {

    String message() default "Image size is too small";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minWidth();

    int minHeight();
}
