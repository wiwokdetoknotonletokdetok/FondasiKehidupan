package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLanguageRepository extends JpaRepository<BookLanguage, Integer> {

    Optional<BookLanguage> findByLanguageIgnoreCase(String language);

    @Query("SELECT bl.language FROM BookLanguage bl WHERE LOWER(bl.language) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> findLanguagesByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
