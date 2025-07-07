package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.config.RabbitMQConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.AuthoredBy;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingGenre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthoredByRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingGenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final AuthoredByRepository authoredByRepository;

    private final HavingGenreRepository havingGenreRepository;

    private final BookLanguageRepository bookLanguageRepository;

    private final BookPublisher bookPublisher;
    private final PointService pointService;

    @Override
    @Transactional
    public void createBook(BookRequestDTO dto, UUID currentUserId) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Buku dengan ISBN tersebut sudah ada");
        }

        Publisher newPublisher = new Publisher();
        newPublisher.setName(dto.getPublisherName().trim());

        Publisher publisher = publisherRepository.findByNameIgnoreCase(dto.getPublisherName().trim())
                .orElseGet(() -> publisherRepository.save(newPublisher));

        BookLanguage newLanguage = new BookLanguage();
        newLanguage.setLanguage(dto.getLanguage().trim());

        BookLanguage language = bookLanguageRepository.findByLanguageIgnoreCase(dto.getLanguage().trim())
                .orElseGet(() -> bookLanguageRepository.save(newLanguage));

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setSynopsis(dto.getSynopsis());
        book.setTitle(dto.getTitle());
        book.setBookPicture(dto.getBookPicture());
        book.setTotalPages(dto.getTotalPages());
        book.setPublishedYear(dto.getPublishedYear());
        book.setLanguage(language);
        book.setPublisher(publisher);
        book.setCreatedBy(currentUserId);
        book = bookRepository.save(book);

        for (String authorName : dto.getAuthorNames()) {
            Author newAuthor = new Author();
            newAuthor.setName(authorName.trim());

            Author author = authorRepository.findByNameIgnoreCase(authorName.trim())
                    .orElseGet(() -> authorRepository.save(newAuthor));

            authoredByRepository.save(new AuthoredBy(book, author));
        }

        for (Integer genreId : dto.getGenreIds()) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre tidak ditemukan"));

            havingGenreRepository.save(new HavingGenre(book, genre));
        }

        bookPublisher.sendNewBookMessage("Buku baru ditambahkan dengan judul: " + book.getTitle());

        pointService.addPoints(currentUserId.toString(), 25);
    }

    @Override
    public BookResponseDTO getBookById(UUID idBook) {
        Book book = bookRepository.findById(idBook)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        List<String> authorNames = authorRepository.findAllNamesByBookId(idBook);
        List<String> genreNames = genreRepository.findAllNamesByBookId(idBook);

        return BookResponseDTO.from(book, authorNames, genreNames);
    }

    @Override
    public List<BookSummaryDTO> advancedSearch(String title, String isbn, String author, String genre, String publisher) {
        return bookRepository.advancedSearch(title, isbn, author, genre, publisher)
                .stream()
                .map(BookSummaryDTO::from)
                .toList();
    }
}
