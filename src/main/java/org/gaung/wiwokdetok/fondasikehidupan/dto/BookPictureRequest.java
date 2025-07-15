package org.gaung.wiwokdetok.fondasikehidupan.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.validation.ImageMinSize;
import org.gaung.wiwokdetok.fondasikehidupan.validation.ValidImageMimeType;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPictureRequest {

    @NotNull
    @ValidImageMimeType
    @ImageMinSize(minWidth = 300, minHeight = 450)
    private MultipartFile bookPicture;
}
