package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 17, nullable = false, unique = true)
    private String isbn;

    @Column(name = "synopsis", columnDefinition = "TEXT", nullable = false)
    private String synopsis;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "book_picture", length = 255, nullable = false)
    private String bookPicture;

    private Integer pages;

    @Column(name = "published_year")
    private Integer publishedYear;

    private String language;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "id_publisher", nullable = false)
    private Publisher publisher;
}
