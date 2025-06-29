package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @EmbeddedId
    private ReviewId id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private int rating;

    @ManyToOne
    @MapsId("idBook")
    @JoinColumn(name = "id_book")
    private Book book;

    public Review(UUID userId, Book book, String message, int rating) {
        this.book = book;
        this.message = message;
        this.rating = rating;
        this.id = new ReviewId(userId, book.getId());
    }
}

