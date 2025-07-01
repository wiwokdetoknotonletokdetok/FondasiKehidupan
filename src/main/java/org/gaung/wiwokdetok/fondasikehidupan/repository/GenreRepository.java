package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("""
        SELECT g FROM Genre g
        JOIN HavingGenre hg ON g.id = hg.genre.id
        WHERE hg.book.id = :bookId
    """)
    List<Genre> findAllByBookId(@Param("bookId") Long bookId);
    @Query("""
        SELECT g.genre FROM Genre g
        JOIN HavingGenre hg ON g.id = hg.genre.id
        WHERE hg.book.id = :bookId
    """)
    List<String> findAllNamesByBookId(@Param("bookId") Long bookId);
}
