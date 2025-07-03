package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.AuthoredBy;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.HavingGenre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthoredByRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingGenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Buku dengan ISBN tersebut sudah ada");
        }

        Publisher publisher = publisherRepository.findByNameIgnoreCase(dto.getPublisherName().trim())
                .orElseGet(() -> publisherRepository.save(new Publisher(null, dto.getPublisherName().trim())));

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setSynopsis(dto.getSynopsis());
        book.setTitle(dto.getTitle());

        book.setRating(0.0f);
        book.setBookPicture(dto.getBookPicture());
        book.setPages(dto.getPages());
        book.setPublishedYear(dto.getPublishedYear());
        book.setLanguage(dto.getLanguage());
        book.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        book.setPublisher(publisher);

        book = bookRepository.save(book);

        List<String> authors = new ArrayList<>();
        for (String authorName : dto.getAuthorNames()) {
            Author author = authorRepository.findByNameIgnoreCase(authorName.trim())
                    .orElseGet(() -> authorRepository.save(new Author(UUID.randomUUID(), authorName.trim())));
            authoredByRepository.save(new AuthoredBy(book, author));
            authors.add(author.getName());
        }

        List<String> genres = new ArrayList<>();
        for (Long genreId : dto.getGenreIds()) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre tidak ditemukan"));
            havingGenreRepository.save(new HavingGenre(book, genre));
            genres.add(genre.getGenre());
        }

        return BookResponseDTO.from(book, authors, genres);
    }

    @Override
    public List<BookSummaryDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookSummaryDTO::from)
                .toList();
    }

    @Override
    public BookResponseDTO getBookById(Long idBook) {
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