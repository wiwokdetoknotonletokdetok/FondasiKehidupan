package org.gaung.wiwokdetok.fondasikehidupan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequest {

    @Size(max = 17)
    private String isbn;

    @Size(max = 255)
    private String title;

    private String synopsis;

    @Min(1)
    private Integer totalPages;

    @Min(0)
    private Integer publishedYear;

    private String language;

    @Size(max = 50)
    private String publisherName;

    private List<@NotBlank @Size(max = 50) String> authorNames;

    private List<@NotNull Integer> genreIds;
}
