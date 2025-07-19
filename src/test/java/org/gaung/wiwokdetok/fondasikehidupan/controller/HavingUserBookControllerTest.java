package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.HavingUserBookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.gaung.wiwokdetok.fondasikehidupan.service.HavingUserBookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HavingUserBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

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
    private HavingUserBookRepository havingUserBookRepository;

    @Autowired
    private HavingUserBookService havingUserBookService;

    private UUID userId;

    private Author author;

    private Genre genre;

    private Genre genre2;

    private Genre genre3;

    private Publisher publisher;

    private Book book;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        author = new Author();
        author.setName("author");
        authorRepository.save(author);

        genre = new Genre();
        genre.setGenreName("genre 1");
        genreRepository.save(genre);

        genre2 = new Genre();
        genre2.setGenreName("genre 2");
        genreRepository.save(genre2);

        genre3 = new Genre();
        genre3.setGenreName("genre 3");
        genreRepository.save(genre3);

        publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);

        BookLanguage language = new BookLanguage();
        language.setLanguage("Indonesia");
        bookLanguageRepository.save(language);

        book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("978-3-16-148410-3");
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
        havingUserBookRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        publisherRepository.deleteAll();
        bookLanguageRepository.deleteAll();
    }

    @Test
    void testAddBookToUser_Success() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        mockMvc.perform(post("/users/me/books/" + book.getId())
                .header("Authorization", "Bearer valid.token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value("Buku berhasil ditambahkan ke koleksi"))
        .andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals("Buku berhasil ditambahkan ke koleksi", response.getData());
        });
    }

    @Test
    void testAddBookToUserFailed_BookNotFound() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        UUID nonExistentBookId = UUID.randomUUID();

        mockMvc.perform(post("/users/me/books/{bookId}", nonExistentBookId)
                .header("Authorization", "Bearer valid.token.here")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
        .andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<?>>() {});

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRemoveBookFromCollectionSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        havingUserBookService.addBookToUser(userId, book.getId());

        mockMvc.perform(
                delete("/users/me/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals("Buku berhasil dihapus dari koleksi", response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetUserBookCollectionSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        mockMvc.perform(post("/users/me/books/{bookId}", book.getId())
                        .header("Authorization", "Bearer valid.token.here")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                get("/users/{userId}/books", userId)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<BookSummaryDTO>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testRemoveBookFromUserCollection_NotFound() throws Exception {
        UUID bookId = UUID.randomUUID();
        Claims claims = mock(Claims.class);

        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        mockMvc.perform(
                delete("/users/me/books/{bookId}", bookId)
                        .header("Authorization", "Bearer valid.token")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCountUserBooksSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        mockMvc.perform(post("/users/me/books/" + book.getId())
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                get("/users/{userId}/books/count", userId)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Integer> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );
            assertEquals(1, response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testAddUserBookCollectionUnauthorized() throws Exception {
        when(jwtUtil.decodeToken("invalid.token"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                post("/users/me/books/{bookId}", book.getId())
                        .header("Authorization", "Bearer invalid.token")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testAddBookToUserConflict() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        mockMvc.perform(post("/users/me/books/{bookId}", book.getId())
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/users/me/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
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
    void testRemoveBookFromCollectionNotFound() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        mockMvc.perform(
                delete("/users/me/books/{bookId}", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
