package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidImageMimeTypeValidator implements ConstraintValidator<ValidImageMimeType, MultipartFile> {

    private Set<String> allowedTypes;

    @Override
    public void initialize(ValidImageMimeType constraintAnnotation) {
        allowedTypes = new HashSet<>(Arrays.asList(constraintAnnotation.allowedTypes()));
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && allowedTypes.contains(contentType.toLowerCase());
    }
}
