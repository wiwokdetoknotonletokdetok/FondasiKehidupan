package org.gaung.wiwokdetok.fondasikehidupan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    private Long bookId;

    @NotBlank(message = "Pesan review tidak boleh kosong")
    private String message;

    @Min(value = 0, message = "Rating minimal adalah 0")
    @Max(value = 5, message = "Rating maksimal adalah 5")
    private Integer rating;
}
