package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "book_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point location;

    @ManyToOne
    @JoinColumn(name = "id_book", nullable = false)
    private Book book;
}