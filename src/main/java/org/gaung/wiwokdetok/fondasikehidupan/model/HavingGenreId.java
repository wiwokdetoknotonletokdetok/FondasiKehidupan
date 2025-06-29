package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HavingGenreId implements Serializable {
    private Long idBook;
    private Long idGenre;
}