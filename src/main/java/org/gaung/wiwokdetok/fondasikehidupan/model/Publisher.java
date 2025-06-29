package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "publisher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false)
    private String name;
}
