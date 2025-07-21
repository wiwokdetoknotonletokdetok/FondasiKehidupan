package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;

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

    private int totalPages;

    private int publishedYear;

    private float totalRatings;

    private float totalReviews;

    private String bookPicture;

    private String publisherName;

    private String language;

    private List<String> authorNames;

    private List<Genre> genres;

    public static BookResponseDTO from(Book book, List<String> authorNames, List<Genre> genres) {
        return new BookResponseDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getSynopsis(),
                book.getTotalPages(),
                book.getPublishedYear(),
                getAverageRating(book),
                book.getTotalReviews(),
                book.getBookPicture(),
                book.getPublisher().getName(),
                book.getLanguage().getLanguage(),
                authorNames,
                genres
        );
    }

    public static float getAverageRating(Book book) {
        int totalReviews = book.getTotalReviews();

        if (totalReviews == 0) {
            return 0.0f;
        }

        return (float) book.getTotalRatings() / totalReviews;
    }
}
