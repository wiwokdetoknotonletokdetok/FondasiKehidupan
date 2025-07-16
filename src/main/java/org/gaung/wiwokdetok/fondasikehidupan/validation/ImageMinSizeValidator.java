package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageMinSizeValidator implements ConstraintValidator<ImageMinSize, MultipartFile> {

    private int minWidth;

    private int minHeight;

    @Override
    public void initialize(ImageMinSize constraintAnnotation) {
        this.minWidth = constraintAnnotation.minWidth();
        this.minHeight = constraintAnnotation.minHeight();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return false;
            }
            return image.getWidth() >= minWidth && image.getHeight() >= minHeight;
        } catch (IOException e) {
            return false;
        }
    }
}
