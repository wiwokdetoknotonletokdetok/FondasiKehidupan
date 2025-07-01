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
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

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
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        ReviewId id = new ReviewId(dto.getUserId(), dto.getBookId());

        if (reviewRepository.existsById(id)) {
            throw new RuntimeException("User has already reviewed this book");
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
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setMessage(dto.getMessage());
        review.setRating(dto.getRating());
        review.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        review = reviewRepository.save(review);

        return toResponseDTO(review);
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
