package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.gaung.wiwokdetok.fondasikehidupan.service.HavingUserBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class HavingUserBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HavingUserBookService service;

    private UUID userId;

    private UUID bookId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        bookId = UUID.randomUUID();
    }

    @Test
    void testAddBookToUserSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        mockMvc.perform(
                post("/users/me/books/{bookId}", bookId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getData());
            assertEquals("Buku berhasil ditambahkan ke koleksi", response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testRemoveBookFromCollectionSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(userId);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        mockMvc.perform(
                delete("/users/me/books/{bookId}", bookId)
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

        List<BookSummaryDTO> books = List.of(
                new BookSummaryDTO(
                        UUID.randomUUID(),
                        "Book Title",
                        "978-0-306-40615-7",
                        4.5f,
                        "https://example.com/pic.jpg",
                        "Publisher Name",
                        List.of("Author One", "Author Two"),
                        List.of("Genre One", "Genre Two")
                )
        );

        when(service.getUserBookCollection(userId)).thenReturn(books);

        mockMvc.perform(
                get("/users/{userId}/books", userId)
                        .header("Authorization", "Bearer valid.token.here")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<BookSummaryDTO>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNotNull(response.getData());
            assertEquals(1, response.getData().size());

            BookSummaryDTO book = response.getData().get(0);
            assertEquals("Book Title", book.getTitle());
            assertEquals("978-0-306-40615-7", book.getIsbn());
            assertEquals("Publisher Name", book.getPublisherName());
            assertEquals("https://example.com/pic.jpg", book.getBookPicture());
            assertEquals(4.5f, book.getRating());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCountUserBooksSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        when(service.getTotalBookCollection(userId)).thenReturn(3);

        mockMvc.perform(
                get("/users/{userId}/books/count", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Integer> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );
            assertEquals(3, response.getData());
            assertNull(response.getErrors());
        });
    }


    @Test
    void testGetUserBookCollectionUnauthorized() throws Exception {
        when(jwtUtil.decodeToken("invalid.token"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                post("/users/me/books/{bookId}", bookId)
                        .header("Authorization", "Bearer invalid.token")
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

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Buku sudah ada di koleksi"))
                .when(service).addBookToUser(userId, bookId);

        mockMvc.perform(
                post("/users/me/books/{bookId}", bookId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token")
        ).andExpect(
                status().isConflict()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );
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

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"))
                .when(service).removeBookFromUserCollection(userId, bookId);

        mockMvc.perform(
                delete("/users/me/books/{bookId}", bookId)
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

    @Test
    void testCountUserBooksNotFound() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(payload);
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        when(service.getTotalBookCollection(userId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        mockMvc.perform(
                get("/users/{userId}/books/count", userId)
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
