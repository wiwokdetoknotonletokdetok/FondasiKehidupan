package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthoredByRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingGenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private BookPublisher bookPublisher;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthoredByRepository authoredByRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private HavingGenreRepository havingGenreRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookLanguageRepository bookLanguageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Author author;

    private Genre genre;

    private Publisher publisher;

    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("author");
        authorRepository.save(author);

        genre = new Genre();
        genre.setGenre("genre");
        genreRepository.save(genre);

        publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);

        BookLanguage language = new BookLanguage();
        language.setLanguage("Indonesia");
        bookLanguageRepository.save(language);

        book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("978-3-16-148410-0");
        book.setSynopsis("Book Synopsis");
        book.setBookPicture("https://example.com");
        book.setPublisher(publisher);
        book.setLanguage(language);
        bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        authoredByRepository.deleteAll();
        havingGenreRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        publisherRepository.deleteAll();
        bookLanguageRepository.deleteAll();
    }

    @Test
    void testCreateBookSuccess_authorAndPublisherAreAvailable() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(anyString());


        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
        bookRequest.setPublisherName(publisher.getName());
        bookRequest.setTotalPages(100);
        bookRequest.setLanguage("Indonesia");
        bookRequest.setPublishedYear(2020);
        bookRequest.setAuthorNames(List.of(author.getName()));
        bookRequest.setGenreIds(List.of(genre.getId()));

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateBookSuccess_authorAndPublisherAreNotAvailable() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(anyString());

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
        bookRequest.setTotalPages(100);
        bookRequest.setLanguage("Indonesia");
        bookRequest.setPublishedYear(2020);
        bookRequest.setPublisherName("New publisher");
        bookRequest.setAuthorNames(List.of("New author", "New author2"));
        bookRequest.setGenreIds(List.of(genre.getId()));

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateBookFailed_userIsUnauthorized() throws Exception {
        when(jwtUtil.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
        bookRequest.setTotalPages(100);
        bookRequest.setLanguage("Indonesia");
        bookRequest.setPublishedYear(2020);
        bookRequest.setPublisherName(publisher.getName());
        bookRequest.setAuthorNames(List.of(author.getName()));
        bookRequest.setGenreIds(List.of(genre.getId()));

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpect(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateBookFailed_duplicateIsbn() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
        bookRequest.setTotalPages(100);
        bookRequest.setLanguage("Indonesia");
        bookRequest.setPublishedYear(2020);
        bookRequest.setPublisherName(publisher.getName());
        bookRequest.setAuthorNames(List.of(author.getName()));
        bookRequest.setGenreIds(List.of(genre.getId()));

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isConflict()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetBookByIdSuccess_userIsNotAuthenticated() throws Exception {
        mockMvc.perform(
                get("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetBookByIdSuccess_userIsAuthenticated() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        mockMvc.perform(
                get("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetBookByIdFailed_bookIsNotFound() throws Exception {
        mockMvc.perform(
                get("/books/{bookId}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
