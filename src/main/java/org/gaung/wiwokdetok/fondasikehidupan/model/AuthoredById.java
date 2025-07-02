package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthoredById implements Serializable {
    private Long idBook;
    private UUID idAuthor;
}