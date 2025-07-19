package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserPointsMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.model.Review;
import org.gaung.wiwokdetok.fondasikehidupan.model.ReviewId;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.UserPointsPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.ReviewRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserPointsPublisher userPointsPublisher;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookLanguageRepository bookLanguageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private UUID userId1;

    private UUID userId2;

    private Book book;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        Publisher publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);

        BookLanguage language = new BookLanguage();
        language.setLanguage("Indonesia");
        bookLanguageRepository.save(language);

        book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("978-0-306-40615-7");
        book.setSynopsis("Book Synopsis");
        book.setBookPicture("https://example.com");
        book.setPublisher(publisher);
        book.setLanguage(language);
        book.setCreatedBy(userId1);
        bookRepository.save(book);

        ReviewId reviewId = new ReviewId(userId2, book.getId());

        Review review = new Review();
        review.setId(reviewId);
        review.setBook(book);
        review.setRating(5);
        review.setMessage("Mantap");
        reviewRepository.save(review);
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        publisherRepository.deleteAll();
        bookLanguageRepository.deleteAll();
    }

    @Test
    void testSubmitReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId1);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        doNothing().when(userPointsPublisher).sendUserPointsForReview(any(AmqpUserPointsMessage.class));

        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setMessage("Mantap");
        request.setRating(5);

        mockMvc.perform(
                post("/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
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
    void testUpdateReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId2);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        UpdateReviewRequestDTO request = new UpdateReviewRequestDTO();
        request.setMessage("Mantap");
        request.setRating(5);

        mockMvc.perform(
                patch("/books/{bookId}/reviews/{userId}", book.getId(), userId2)
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testUpdateReviewFail_SomeoneNotAuthorized() throws Exception {
        UUID otherUser = UUID.randomUUID();

        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId2);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        UpdateReviewRequestDTO request = new UpdateReviewRequestDTO();
        request.setMessage("Mantap");
        request.setRating(5);

        mockMvc.perform(
                patch("/books/{bookId}/reviews/{userId}", book.getId(), otherUser)
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isForbidden()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetReviewsForBookSuccessWithoutAuth() throws Exception {
        mockMvc.perform(
                get("/books/{bookId}/reviews", book.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ReviewResponseDTO>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(1, response.getData().size());
            assertEquals("Mantap", response.getData().getFirst().getMessage());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testDeleteReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId2);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        mockMvc.perform(
                delete("/books/{bookId}/reviews/{usedId}", book.getId(), userId2)
                        .header("Authorization", "Bearer valid.token")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK", response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testDeleteReview_ReviewNotFound() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId2);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        UUID fakeBookId = UUID.randomUUID();

        mockMvc.perform(
                delete("/books/{bookId}/reviews/{userId}", fakeBookId, userId2)
                        .header("Authorization", "Bearer valid.token")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUnauthorizedAccessSubmitReview() throws Exception {
        when(jwtUtil.decodeToken("invalid.token"))
                .thenThrow(new JwtException("Invalid token"));

        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setMessage("Mantap");
        request.setRating(5);

        mockMvc.perform(
                post("/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer invalid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
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
    void testSubmitReview_BookNotFound() throws Exception {
        UUID bookId = UUID.randomUUID();

        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId1);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        doNothing().when(userPointsPublisher).sendUserPointsForReview(any(AmqpUserPointsMessage.class));

        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setMessage("Mantap");
        request.setRating(5);

        mockMvc.perform(
                post("/books/{bookId}/reviews", bookId)
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testSubmitReview_AlreadyExists() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId1);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        doNothing().when(userPointsPublisher).sendUserPointsForReview(any(AmqpUserPointsMessage.class));

        ReviewRequestDTO request1 = new ReviewRequestDTO();
        request1.setMessage("Mantap");
        request1.setRating(4);

        ReviewRequestDTO request2 = new ReviewRequestDTO();
        request2.setMessage("Second");
        request2.setRating(4);

        mockMvc.perform(post("/books/" + book.getId() + "/reviews")
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/books/{bookId}/reviews", book.getId())
                        .header("Authorization", "Bearer valid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
