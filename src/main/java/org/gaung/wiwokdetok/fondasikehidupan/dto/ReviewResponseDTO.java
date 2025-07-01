package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private UUID userId;
    private Long bookId;
    private String message;
    private int rating;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}