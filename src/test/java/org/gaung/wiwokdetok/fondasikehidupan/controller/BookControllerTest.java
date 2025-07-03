package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Author;
import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthorRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.AuthoredByRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

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
    private ObjectMapper objectMapper;

    private Author author;

    private Genre genre;

    private Publisher publisher;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(UUID.randomUUID());
        author.setName("author");
        authorRepository.save(author);

        genre = new Genre();
        genre.setGenre("genre");
        genreRepository.save(genre);

        publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);
    }

    @AfterEach
    void tearDown() {
        authoredByRepository.deleteAll();
        havingGenreRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        publisherRepository.deleteAll();
    }

    @Test
    void testCreateBookSuccessWhenAuthorAndPublisherAreAvailable() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
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
                status().isCreated()
        ).andDo(result -> {
            // TODO: Created seharusnya response string aja
            // TODO: BookResponse seharusnya tampilkan informasi buku aja. Author, publisher, genre name boleh ditampilkan, tapi location gak perlu, book location bisa banyak
            WebResponse<BookResponseDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateBookSuccessWhenAuthorAndPublisherAreNotAvailable() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
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
            // TODO: Created seharusnya response string aja
            // TODO: BookResponse seharusnya tampilkan informasi buku aja. Author, publisher, genre name boleh ditampilkan, tapi location gak perlu, book location bisa banyak
            WebResponse<BookResponseDTO> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateBookFailedWhenUserIsUnauthorized() throws Exception {
        when(jwtUtil.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("Book Title");
        bookRequest.setIsbn("978-0-306-40615-7");
        bookRequest.setSynopsis("Book Synopsis");
        bookRequest.setBookPicture("https://example.com");
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
}
