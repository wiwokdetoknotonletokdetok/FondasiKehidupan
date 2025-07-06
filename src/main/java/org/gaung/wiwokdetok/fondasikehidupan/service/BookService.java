package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface BookService {
    void createBook(BookRequestDTO dto, String id);
    BookResponseDTO getBookById(UUID idBook);
    List<BookSummaryDTO> getAllBooks();
    List<BookSummaryDTO> findByTitleContainingIgnoreCase(String keyword);
    List<BookSummaryDTO> advancedSearch(String title, String isbn, String author, String genre, String publisher);
}
