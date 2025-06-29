package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String name;
}
