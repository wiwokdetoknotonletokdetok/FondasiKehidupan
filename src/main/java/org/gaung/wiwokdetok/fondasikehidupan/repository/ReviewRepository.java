package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Review;
import org.gaung.wiwokdetok.fondasikehidupan.model.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    List<Review> findByBookId(UUID bookId);

    boolean existsById(ReviewId id);
}
