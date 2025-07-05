package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateReviewRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    void submitReview(UUID currentUserId, UUID bookId, ReviewRequestDTO request);

    void updateReview(UUID currentUserId, UUID userId, UUID bookId, UpdateReviewRequestDTO request);

    List<ReviewResponseDTO> getReviewsForBook(UUID bookId);

    void deleteReview(UUID currentUserId, UUID userId, UUID bookId);
}
