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
@Table(name = "authored_by")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthoredBy {

    @EmbeddedId
    private AuthoredById id;

    @ManyToOne
    @MapsId("idBook")
    @JoinColumn(name = "id_book")
    private Book book;

    @ManyToOne
    @MapsId("idAuthor")
    @JoinColumn(name = "id_author")
    private Author author;

    public AuthoredBy(Book book, Author author) {
        this.book = book;
        this.author = author;
        this.id = new AuthoredById(book.getId(), author.getId());
    }
}
