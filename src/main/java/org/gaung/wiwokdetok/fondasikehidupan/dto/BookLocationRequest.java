package org.gaung.wiwokdetok.fondasikehidupan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLocationRequest {

    @NotBlank
    private String locationName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
