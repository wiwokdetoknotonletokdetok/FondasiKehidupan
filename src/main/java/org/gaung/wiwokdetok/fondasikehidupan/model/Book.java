package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 17, nullable = false, unique = true)
    private String isbn;

    @Column(name = "synopsis", columnDefinition = "TEXT", nullable = false)
    private String synopsis;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "DECIMAL(2,1)")
    private float rating;

    @Column(name = "book_picture", length = 255, nullable = false)
    private String bookPicture;

    @Column(name = "total_pages", nullable = false)
    private int totalPages;

    @Column(name = "total_reviews")
    private int totalReviews;

    @Column(name = "published_year")
    private int publishedYear;

    @ManyToOne
    @JoinColumn(name = "id_language", nullable = false)
    private BookLanguage language;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "id_publisher", nullable = false)
    private Publisher publisher;
}
