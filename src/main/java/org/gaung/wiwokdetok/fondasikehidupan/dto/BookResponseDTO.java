package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String isbn;
    private String title;
    private String description;
    private BigDecimal rating;
    private String bookPicture;
    private UUID idPublisher;

    private String publisherName;
    private List<String> authorNames;
    private List<String> genreNames;
    private String locationName;
    private double latitude;
    private double longitude;

    public static BookResponseDTO from(Book book, List<String> authorNames, List<String> genreNames, BookLocation location) {
        return new BookResponseDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getSynopsis(),
                book.getRating(),
                book.getBookPicture(),
                book.getPublisher().getId(),
                book.getPublisher().getName(),
                authorNames,
                genreNames,
                location != null ? location.getLocationName() : null,
                location != null && location.getLocation() != null ? location.getLocation().getY() : 0.0,
                location != null && location.getLocation() != null ? location.getLocation().getX() : 0.0
        );
    }
}