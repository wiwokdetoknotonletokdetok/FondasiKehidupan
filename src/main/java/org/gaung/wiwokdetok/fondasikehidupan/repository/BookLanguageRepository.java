package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookLanguageRepository extends JpaRepository<BookLanguage, Integer> {

    Optional<BookLanguage> findByLanguageIgnoreCase(String language);
}
