package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "book_picture", length = 255, nullable = false)
    private String bookPicture;

    @Column(name = "id_publisher", nullable = false)
    private UUID idPublisher;
}
