package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.Review;
import org.gaung.wiwokdetok.fondasikehidupan.model.ReviewId;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    public List<ReviewResponseDTO> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponseDTO submitReview(ReviewRequestDTO dto) {
        if (dto.getBookId() == null || dto.getMessage() == null ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        ReviewId id = new ReviewId(dto.getUserId(), dto.getBookId());

        if (reviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User sudah memberikan review");
        }

        Review review = new Review(dto.getUserId(), book, dto.getMessage(), dto.getRating());

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        review = reviewRepository.save(review);

        return toResponseDTO(review);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(ReviewRequestDTO dto) {
        ReviewId id = new ReviewId(dto.getUserId(), dto.getBookId());

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review tidak ditemukan"));

        review.setMessage(dto.getMessage());
        review.setRating(dto.getRating());
        review.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        review = reviewRepository.save(review);

        return toResponseDTO(review);
    }

    @Override
    public void deleteReview(UUID userId, Long bookId) {
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
