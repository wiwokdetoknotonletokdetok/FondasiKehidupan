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
public class BookResponseDTO {

    private UUID id;

    private String isbn;

    private String title;

    private String synopsis;

    private float rating;

    private String bookPicture;

    private String publisherName;

    private List<String> authorNames;

    private List<String> genreNames;

    public static BookResponseDTO from(Book book, List<String> authorNames, List<String> genreNames) {
        return new BookResponseDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getSynopsis(),
                book.getRating(),
                book.getBookPicture(),
                book.getPublisher().getName(),
                authorNames,
                genreNames
        );
    }
}
