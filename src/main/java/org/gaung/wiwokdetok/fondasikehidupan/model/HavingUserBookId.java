package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HavingUserBookId implements Serializable {
    private UUID idUser;
    private Long idBook;
}