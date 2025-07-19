package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookRequest;

import java.util.List;
import java.util.UUID;

public interface BookService {

    void createBook(BookRequestDTO dto, UUID userId);

    BookResponseDTO getBookById(UUID idBook, UUID userId);

    List<BookSummaryDTO> advancedSearch(String title, String isbn, String author, String genre, String publisher);

    void updateBook(UUID bookId, UpdateBookRequest request);
}
