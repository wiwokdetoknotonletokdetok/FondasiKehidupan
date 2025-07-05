package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Optional<Author> findByNameIgnoreCase(String name);

    @Query("""
        SELECT a FROM Author a
        JOIN AuthoredBy ab ON a.id = ab.author.id
        WHERE ab.book.id = :bookId
    """)
    List<Author> findAllByBookId(@Param("bookId") UUID bookId);

    @Query("""
        SELECT a.name FROM Author a
        JOIN AuthoredBy ab ON a.id = ab.author.id
        WHERE ab.book.id = :bookId
    """)
    List<String> findAllNamesByBookId(@Param("bookId") UUID bookId);
}
