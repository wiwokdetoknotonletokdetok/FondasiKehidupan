package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findByNameIgnoreCase(String name);
    @Query("""
        SELECT a FROM Author a
        JOIN AuthoredBy ab ON a.id = ab.author.id
        WHERE ab.book.id = :bookId
    """)
    List<Author> findAllByBookId(@Param("bookId") Long bookId);
    @Query("""
        SELECT a.name FROM Author a
        JOIN AuthoredBy ab ON a.id = ab.author.id
        WHERE ab.book.id = :bookId
    """)
    List<String> findAllNamesByBookId(@Param("bookId") Long bookId);
}