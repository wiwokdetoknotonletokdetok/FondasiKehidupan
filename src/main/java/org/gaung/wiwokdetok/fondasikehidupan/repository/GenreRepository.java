package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Query("SELECT g.genreName FROM Genre g")
    List<String> findAllGenreNames();

    @Query("""
        SELECT g.genreName FROM Genre g
        JOIN HavingGenre hg ON g.id = hg.genre.id
        WHERE hg.book.id = :bookId
    """)
    List<String> findAllNamesByBookId(@Param("bookId") UUID bookId);
}
