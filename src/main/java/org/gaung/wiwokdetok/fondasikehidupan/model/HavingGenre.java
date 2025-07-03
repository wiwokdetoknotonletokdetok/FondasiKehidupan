package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "having_genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HavingGenre {

    @EmbeddedId
    private HavingGenreId id;

    @ManyToOne
    @MapsId("idBook")
    @JoinColumn(name = "id_book")
    private Book book;

    @ManyToOne
    @MapsId("idGenre")
    @JoinColumn(name = "id_genre")
    private Genre genre;

    public HavingGenre(Book book, Genre genre) {
        this.book = book;
        this.genre = genre;
        this.id = new HavingGenreId(book.getId(), genre.getId());
    }
}