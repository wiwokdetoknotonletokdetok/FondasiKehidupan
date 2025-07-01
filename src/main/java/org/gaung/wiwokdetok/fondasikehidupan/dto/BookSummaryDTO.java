package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLocation;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSummaryDTO {
    private Long id;
    private String title;
    private String isbn;
    private BigDecimal rating;
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
