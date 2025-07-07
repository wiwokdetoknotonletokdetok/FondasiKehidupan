package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.config.RabbitMQConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.Review;
import org.gaung.wiwokdetok.fondasikehidupan.model.ReviewId;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.ReviewPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final BookRepository bookRepository;
    private final PointService pointService;
    private final ReviewPublisher reviewPublisher;

    @Override
    public List<ReviewResponseDTO> getReviewsForBook(UUID bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void submitReview(UUID currentUserId, UUID bookId, ReviewRequestDTO request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        ReviewId id = new ReviewId(currentUserId, bookId);

        if (reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User sudah memberikan review");
        }

        createNewReview(id, book, request);

        reviewPublisher.sendNewReviewMessage("Review baru untuk buku: " + book.getTitle());

        pointService.addPoints(currentUserId.toString(), 10);
    }

    private void createNewReview(ReviewId id, Book book, ReviewRequestDTO request) {
        Review review = new Review();
        review.setId(id);
        review.setMessage(request.getMessage());
        review.setRating(request.getRating());
        review.setBook(book);
        review.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void updateReview(UUID currentUserId, UUID userId, UUID bookId, UpdateReviewRequestDTO request) {
        validateUserOwnership(currentUserId, userId);

        ReviewId id = new ReviewId(userId, bookId);

        Review review = getReviewById(id);

        Optional.ofNullable(request.getMessage())
                .ifPresent(review::setMessage);

        Optional.ofNullable(request.getRating())
                .ifPresent(review::setRating);

        review.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        reviewRepository.save(review);

        reviewPublisher.sendUpdateReviewMessage("Review untuk buku '" + review.getBook().getTitle() + "' diperbarui.");
    }

    private Review getReviewById(ReviewId id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review tidak ditemukan"));
    }

    private void validateUserOwnership(UUID currentUserId, UUID userId) {
        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    @Override
    public void deleteReview(UUID currentUserId, UUID userId, UUID bookId) {
        validateUserOwnership(currentUserId, userId);

        ReviewId id = new ReviewId(userId, bookId);
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId().getIdUser(),
                review.getId().getIdBook(),
                review.getMessage(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
