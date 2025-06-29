package org.gaung.wiwokdetok.fondasikehidupan.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String genre;
}