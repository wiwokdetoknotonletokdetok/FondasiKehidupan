package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBook;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingUserBookId;
import org.gaung.wiwokdetok.fondasikehidupan.projection.BookAuthorGenreProjection;
import org.gaung.wiwokdetok.fondasikehidupan.mapper.BookSummaryDTOMapper;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingUserBookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HavingUserBookServiceImpl implements HavingUserBookService {

    private final HavingUserBookRepository repository;

    private final BookRepository bookRepository;

    private final BookSummaryDTOMapper bookSummaryDTOMapper;

    @Override
    public void addBookToUser(UUID userId, UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        HavingUserBookId id = new HavingUserBookId(userId, bookId);

        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Buku sudah ada dalam koleksi user ini.");
        }

        HavingUserBook userBook = new HavingUserBook(userId, book);
        repository.save(userBook);
    }

    @Override
    public List<BookSummaryDTO> getUserBookCollection(UUID userId) {
        System.out.println("TESTJKANJKNS");
        List<BookAuthorGenreProjection> rows = bookRepository.findUserBooksWithSummary(userId);
        return bookSummaryDTOMapper.groupFromProjections(rows);
    }

    @Override
    public void removeBookFromUserCollection(UUID userId, UUID bookId) {
        HavingUserBookId id = new HavingUserBookId(userId, bookId);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ada dalam koleksi user ini.");
        }
        repository.deleteById(id);
    }

    @Override
    public int getTotalBookCollection(UUID userId) {
        return repository.countBooksByUser(userId);
    }
}
