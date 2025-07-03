package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    void submitReview(UUID userId, ReviewRequestDTO dto);
    void updateReview(UUID userId, ReviewRequestDTO dto);
    List<ReviewResponseDTO> getReviewsForBook(Long bookId);
    void deleteReview(UUID userId, Long bookId);
}
