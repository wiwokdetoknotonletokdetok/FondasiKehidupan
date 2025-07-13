package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSummaryDTO {

    private UUID id;

    private String title;

    private String isbn;

    private float rating;

    private String bookPicture;

    private String publisherName;

    private List<String> authorNames;

    private List<String> genreNames;
}
