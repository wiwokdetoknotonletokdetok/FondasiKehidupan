package org.gaung.wiwokdetok.fondasikehidupan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    @NotBlank(message = "ISBN tidak boleh kosong")
    @Size(max = 17)
    private String isbn;

    @NotBlank(message = "Judul buku tidak boleh kosong")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Sinopsis tidak boleh kosong")
    private String synopsis;

    @NotBlank(message = "Gambar buku tidak boleh kosong")
    private String bookPicture;

    @Min(1)
    private Integer pages;

    @Min(0)
    private Integer publishedYear;

    private String language;

    @NotBlank
    @Size(max = 50)
    private String publisherName;

    @NotEmpty(message = "Nama penulis tidak boleh kosong")
    private List<@NotBlank String> authorNames;

    @NotEmpty(message = "Genre tidak boleh kosong")
    private List<@NotNull Long> genreIds;
}
