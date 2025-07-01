package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponseDTO submitReview(ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(ReviewRequestDTO dto);
    List<ReviewResponseDTO> getReviewsForBook(Long bookId);
    void deleteReview(UUID userId, Long bookId);
}
