package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;

import java.util.List;

public interface BookService {
    BookResponseDTO createBook(BookRequestDTO dto);
    List<BookSummaryDTO> getAllBooks();
    List<BookSummaryDTO> findByTitleContainingIgnoreCase(String keyword);
    BookResponseDTO getBookById(Long idBook);
    List<BookSummaryDTO> advancedSearch(String title, String isbn, String author, String genre, String publisher);
    void addPoints(String token, int points);
}