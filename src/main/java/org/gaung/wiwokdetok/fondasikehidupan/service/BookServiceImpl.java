package org.gaung.wiwokdetok.fondasikehidupan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookRequest;
import org.gaung.wiwokdetok.fondasikehidupan.mapper.BookSummaryDTOMapper;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.projection.BookAuthorGenreProjection;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookLanguageRepository bookLanguageRepository;

    private final BookPublisher bookPublisher;

    @Override
    @Transactional
    public void createBook(BookRequestDTO dto, UUID userId) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Buku dengan ISBN tersebut sudah ada");
        }

        Publisher publisher = handleBookPublisher(dto.getPublisherName());

        BookLanguage language = handleBookLanguage(dto.getLanguage());

        List<Author> authors = handleBookAuthors(dto.getAuthorNames());

        List<Genre> genres = handleBookGenres(dto.getGenreIds());

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setSynopsis(dto.getSynopsis());
        book.setTitle(dto.getTitle());
        book.setBookPicture(dto.getBookPicture());
        book.setTotalPages(dto.getTotalPages());
        book.setPublishedYear(dto.getPublishedYear());
        book.setLanguage(language);
        book.setAuthors(authors);
        book.setPublisher(publisher);
        book.setGenres(genres);
        book.setCreatedBy(userId);
        bookRepository.save(book);

        AmqpBookMessage message = createBookMessage(book);
        bookPublisher.sendNewBookMessage(message);
    }

    private AmqpBookMessage createBookMessage(Book book) {
        AmqpBookMessage message = new AmqpBookMessage();
        message.setId(book.getId());
        message.setTitle(book.getTitle());
        message.setSynopsis(book.getSynopsis());
        message.setBookPicture(book.getBookPicture());
        message.setCreatedBy(book.getCreatedBy());

        return message;
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
        List<BookAuthorGenreProjection> rows = bookRepository.advancedSearch(title, isbn, author, genre, publisher);
        return BookSummaryDTOMapper.groupFromProjections(rows);
    }

    @Override
    public void updateBook(UUID bookId, UUID stringId, UpdateBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        validateIsbnChange(book, request.getIsbn());

        updateNonNullBookFields(book, request);

        if (request.getAuthorNames() != null) {
            List<Author> authors = handleBookAuthors(request.getAuthorNames());
            book.setAuthors(authors);
        }

        if (request.getGenreIds() != null) {
            List<Genre> genres = handleBookGenres(request.getGenreIds());
            book.setGenres(genres);
        }

        if (request.getPublisherName() != null) {
            Publisher publisher = handleBookPublisher(request.getPublisherName());
            book.setPublisher(publisher);
        }

        if (request.getLanguage() != null) {
            BookLanguage language = handleBookLanguage(request.getLanguage());
            book.setLanguage(language);
        }

        bookRepository.save(book);

        if (request.getTitle() != null || request.getSynopsis() != null) {
            AmqpBookMessage message = createBookMessage(book);
            bookPublisher.sendUpdateBookMessage(message);
        }
    }

    private List<Author> handleBookAuthors(List<String> authorNames) {
        List<Author> authors = new ArrayList<>();

        for (String authorName : authorNames) {
            String formattedName = capitalizeWords(authorName);

            Author newAuthor = new Author();
            newAuthor.setName(formattedName);

            Author author = authorRepository.findByNameIgnoreCase(formattedName)
                    .orElseGet(() -> authorRepository.save(newAuthor));

            authors.add(author);
        }

        return authors;
    }

    private List<Genre> handleBookGenres(List<Integer> genreIds) {
        List<Genre> genres = new ArrayList<>();

        for (int genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre tidak ditemukan"));

            genres.add(genre);
        }

        return genres;
    }

    public Publisher handleBookPublisher(String publisher) {
        String formattedPublisher = capitalizeWords(publisher);

        Publisher newPublisher = new Publisher();
        newPublisher.setName(formattedPublisher);

        return publisherRepository.findByNameIgnoreCase(formattedPublisher)
                .orElseGet(() -> publisherRepository.save(newPublisher));
    }

    private BookLanguage handleBookLanguage(String language) {
        String formattedLanguage = capitalizeWords(language);

        BookLanguage newLanguage = new BookLanguage();
        newLanguage.setLanguage(formattedLanguage);

        return bookLanguageRepository.findByLanguageIgnoreCase(formattedLanguage)
                .orElseGet(() -> bookLanguageRepository.save(newLanguage));
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return capitalized.toString().trim();
    }

    private void updateNonNullBookFields(Book book, UpdateBookRequest request) {
        Optional.ofNullable(request.getIsbn())
                .ifPresent(book::setIsbn);

        Optional.ofNullable(request.getTitle())
                .ifPresent(book::setTitle);

        Optional.ofNullable(request.getSynopsis())
                .ifPresent(book::setSynopsis);

        Optional.ofNullable(request.getTotalPages())
                .ifPresent(book::setTotalPages);

        Optional.ofNullable(request.getPublishedYear())
                .ifPresent(book::setPublishedYear);
    }

    private void validateIsbnChange(Book book, String newIsbn) {
        if (!book.getIsbn().equals(newIsbn)) {
            if (bookRepository.findByIsbn(newIsbn).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Buku dengan ISBN tersebut sudah ada");
            }
        }
    }
}
