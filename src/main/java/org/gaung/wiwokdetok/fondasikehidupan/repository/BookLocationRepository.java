package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.BookLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookLocationRepository extends JpaRepository<BookLocation, Long> {
    Optional<BookLocation> findByBookId(Long bookId);
}
