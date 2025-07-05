package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO submitReview(ReviewRequestDTO dto, String token);
    ReviewResponseDTO updateReview(ReviewRequestDTO dto);
    List<ReviewResponseDTO> getReviewsForBook(Long bookId);
}
