package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "having_user_book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HavingUserBook {

    @EmbeddedId
    private HavingUserBookId id;

    @ManyToOne
    @MapsId("idBook")
    @JoinColumn(name = "id_book")
    private Book book;

    public HavingUserBook(UUID idUser, Book book) {
        this.book = book;
        this.id = new HavingUserBookId(idUser, book.getId());
    }
}