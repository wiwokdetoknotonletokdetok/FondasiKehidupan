package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;

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

    public static BookSummaryDTO from(Book book) {
        return new BookSummaryDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getRating(),
                book.getBookPicture(),
                book.getPublisher().getName()
        );
    }
}
