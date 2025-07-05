package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
