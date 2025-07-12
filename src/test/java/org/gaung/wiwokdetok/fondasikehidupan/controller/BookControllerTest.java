package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private GenreRepository genreRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookLanguageRepository bookLanguageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Author author;

    private Genre genre;

    private Genre genre2;

    private Genre genre3;

    private Publisher publisher;

    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("author");
        authorRepository.save(author);

        genre = new Genre();
        genre.setGenre("genre 1");
        genreRepository.save(genre);

        genre2 = new Genre();
        genre2.setGenre("genre 2");
        genreRepository.save(genre2);

        genre3 = new Genre();
        genre3.setGenre("genre 3");
        genreRepository.save(genre3);

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
        book.setCreatedBy(UUID.randomUUID());
        book.setGenres(List.of(genre));
        book.setAuthors(List.of(author));
        bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
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

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));


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

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

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

    @Test
    void testUpdateBookSuccess_updateNewIsbn() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        String updatedIsbn = "978-3-16-148410-7";
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setIsbn(updatedIsbn);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedIsbn, updatedBook.getIsbn());
    }

    @Test
    void testUpdateBookSuccess_updateWithSameIsbn() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setIsbn(book.getIsbn());

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(book.getIsbn(), updatedBook.getIsbn());
    }

    @Test
    void testUpdateBookSuccess_updateTitle() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        String updatedTitle = "New Title";
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setTitle(updatedTitle);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedTitle, updatedBook.getTitle());
    }

    @Test
    void testUpdateBookSuccess_updateSynopsis() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        String updatedSynopsis = "New Synopsis";
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setSynopsis(updatedSynopsis);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedSynopsis, updatedBook.getSynopsis());
    }

    @Test
    void testUpdateBookSuccess_updateTotalPages() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        int updatedTotalPages = 150;
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setTotalPages(updatedTotalPages);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedTotalPages, updatedBook.getTotalPages());
    }

    @Test
    void testUpdateBookSuccess_updatePublishedYear() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        int updatedPublishedYear = 150;
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setPublishedYear(updatedPublishedYear);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedPublishedYear, updatedBook.getPublishedYear());
    }

    @Test
    void testUpdateBookSuccess_updateLanguage() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        String updatedLanguage = "New Language";
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setLanguage(updatedLanguage);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedLanguage, updatedBook.getLanguage().getLanguage());
    }

    @Test
    void testUpdateBookSuccess_updatePublisherName() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        String updatedPublisherName = "New Publisher";
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setPublisherName(updatedPublisherName);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(updatedPublisherName, updatedBook.getPublisher().getName());
    }

    @Test
    void testUpdateBookSuccess_updateAuthorNames() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        List<String> updatedAuthorNames = List.of("New Author 1", "New Author 2");
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setAuthorNames(updatedAuthorNames);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        mockMvc.perform(
                get("/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<BookResponseDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());

            assertEquals(updatedAuthorNames.size(), response.getData().getAuthorNames().size());
            assertEquals(updatedAuthorNames.getFirst(), response.getData().getAuthorNames().getFirst());
            assertEquals(updatedAuthorNames.getLast(), response.getData().getAuthorNames().getLast());
        });
    }

    @Test
    void testUpdateBookSuccess_updateAddNewAuthorNames() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        List<String> updatedAuthorNames = List.of(author.getName(), "New Author 1");
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setAuthorNames(updatedAuthorNames);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        mockMvc.perform(
                get("/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<BookResponseDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            assertEquals(updatedAuthorNames.size(), response.getData().getAuthorNames().size());
            assertEquals(updatedAuthorNames.getFirst(), response.getData().getAuthorNames().getFirst());
            assertEquals(updatedAuthorNames.getLast(), response.getData().getAuthorNames().getLast());
        });
    }

    @Test
    void testUpdateBookSuccess_updateGenreIds() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        List<Integer> updatedGenreIds = List.of(genre2.getId(), genre3.getId());
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setGenreIds(updatedGenreIds);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });

        mockMvc.perform(
                get("/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<BookResponseDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            assertEquals(updatedGenreIds.size(), response.getData().getGenreNames().size());
            assertEquals(genre2.getGenre(), response.getData().getGenreNames().getFirst());
            assertEquals(genre3.getGenre(), response.getData().getGenreNames().getLast());
        });
    }

    @Test
    void testUpdateBookFailed_updateNotExistGenreIds() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        doNothing().when(bookPublisher).sendNewBookMessage(any(AmqpBookMessage.class));

        List<Integer> updatedGenreIds = List.of(10214);
        UpdateBookRequest bookRequest = new UpdateBookRequest();
        bookRequest.setGenreIds(updatedGenreIds);

        mockMvc.perform(
                patch("/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .header("Authorization", "Bearer valid.token.here")
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
