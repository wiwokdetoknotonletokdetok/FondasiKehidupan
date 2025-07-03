package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    private String isbn;
    private String title;
    private String synopsis;
    private float rating;
    private String bookPicture;

    private Integer pages;
    private Integer publishedYear;
    private String language;

    private String publisherName;
    private List<String> authorNames;
    private List<Long> genreIds;
}
