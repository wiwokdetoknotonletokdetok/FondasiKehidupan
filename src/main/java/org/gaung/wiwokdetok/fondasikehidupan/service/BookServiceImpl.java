package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.model.*;
import org.gaung.wiwokdetok.fondasikehidupan.repository.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
    private final BookLocationRepository bookLocationRepository;

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        Publisher publisher = publisherRepository.findByNameIgnoreCase(dto.getPublisherName().trim())
                .orElseGet(() -> publisherRepository.save(new Publisher(null, dto.getPublisherName().trim())));

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setSynopsis(dto.getSynopsis());
        book.setTitle(dto.getTitle());

        book.setRating(BigDecimal.ZERO);
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

        return BookResponseDTO.from(book, authors, genres, null);
    }

    @Override
    public List<BookSummaryDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookSummaryDTO::from)
                .toList();
    }

    @Override
    public List<BookSummaryDTO> findByTitleContainingIgnoreCase(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(BookSummaryDTO::from)
                .toList();
    }

    @Override
    public BookResponseDTO getBookById(Long idBook) {
        Book book = bookRepository.findById(idBook)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        BookLocation location = bookLocationRepository.findByBookId(idBook).orElse(null);

        List<String> authorNames = authorRepository.findAllNamesByBookId(idBook);
        List<String> genreNames = genreRepository.findAllNamesByBookId(idBook);

        return BookResponseDTO.from(book, authorNames, genreNames, location);
    }

    @Override
    public List<BookSummaryDTO> advancedSearch(String title, String isbn, String author, String genre, String publisher) {
        return bookRepository.advancedSearch(title, isbn, author, genre, publisher)
                .stream()
                .map(BookSummaryDTO::from)
                .toList();
    }
}