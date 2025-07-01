package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBook;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HavingUserBookRepository extends JpaRepository<HavingUserBook, HavingUserBookId> {
    boolean existsById(HavingUserBookId id);
    void deleteById(HavingUserBookId id);
    @Query("""
        SELECT hub.book 
        FROM HavingUserBook hub
        JOIN hub.book book
        WHERE hub.id.idUser = :userId
    """)
    List<Book> findBooksByUserId(@Param("userId") UUID userId);
    @Query("SELECT COUNT(hub) FROM HavingUserBook hub WHERE hub.id.idUser = :userId")
    int countBooksByUser(@Param("userId") UUID userId);
}