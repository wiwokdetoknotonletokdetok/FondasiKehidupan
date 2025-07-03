package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN b.publisher p
        JOIN AuthoredBy ab ON ab.book = b
        JOIN Author a ON a = ab.author
        JOIN HavingGenre hg ON hg.book = b
        JOIN Genre g ON g = hg.genre
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))
          AND (:author IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%')))
          AND (:genre IS NULL OR LOWER(g.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
          AND (:publisher IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :publisher, '%')))
    """)
    List<Book> advancedSearch(
            @Param("title") String title,
            @Param("isbn") String isbn,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("publisher") String publisher
    );
}