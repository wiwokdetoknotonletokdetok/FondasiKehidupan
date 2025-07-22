package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.projection.BookAuthorGenreProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByIsbn(String isbn);

    @Query(value = """
        SELECT 
            b.id AS bookId,
            b.title AS title,
            b.book_picture AS bookPicture
        FROM book b
        JOIN publisher p ON p.id = b.id_publisher
        JOIN authored_by ab ON ab.id_book = b.id
        JOIN author a ON a.id = ab.id_author
        JOIN having_genre hg ON hg.id_book = b.id
        JOIN genre g ON g.id = hg.id_genre
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))
          AND (:author IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%')))
          AND (:genre IS NULL OR LOWER(g.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
          AND (:publisher IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :publisher, '%')))
    """, nativeQuery = true)
    List<BookAuthorGenreProjection> advancedSearch(
            @Param("title") String title,
            @Param("isbn") String isbn,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("publisher") String publisher
    );

    @Query(value = """
        SELECT\s
            b.id AS bookId,
            b.title AS title,
            b.bookPicture AS bookPicture
        FROM HavingUserBook ub
        JOIN ub.book b
        WHERE ub.id.idUser = :userId
    """)
    List<BookAuthorGenreProjection> findUserBooksWithSummary(@Param("userId") UUID userId);

    Optional<Book> findByIsbn(String isbn);
}
