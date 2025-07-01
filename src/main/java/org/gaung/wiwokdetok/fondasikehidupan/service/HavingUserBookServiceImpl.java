package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBook;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBookId;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingUserBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HavingUserBookServiceImpl implements HavingUserBookService {

    private final HavingUserBookRepository repository;
    private final BookRepository bookRepository;

    @Override
    public void addBookToUser(UUID userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        HavingUserBookId id = new HavingUserBookId(userId, bookId);
        if (repository.existsById(id)) {
            throw new RuntimeException("Book already in user collection");
        }

        HavingUserBook userBook = new HavingUserBook(userId, book);
        repository.save(userBook);
    }

    @Override
    public List<BookSummaryDTO> getUserBookCollection(UUID userId) {
        List<Book> books = repository.findBooksByUserId(userId);
        return books.stream()
                .map(BookSummaryDTO::from)
                .toList();
    }

    @Override
    public void removeBookFromUserCollection(UUID userId, Long bookId) {
        HavingUserBookId id = new HavingUserBookId(userId, bookId);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Buku tidak ada dalam koleksi user ini.");
        }
        repository.deleteById(id);
    }

    @Override
    public int getTotalBookCollection(UUID userId) {
        return repository.countBooksByUser(userId);
    }
}
