package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.gaung.wiwokdetok.fondasikehidupan.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
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
    private ReviewService reviewService;

    private UUID userId;
    private Long bookId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        bookId = 1L;
    }

    @Test
    void testSubmitReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        ReviewRequestDTO request = new ReviewRequestDTO(bookId, "Mantap", 5);
        doNothing().when(reviewService).submitReview(any(UUID.class), any(ReviewRequestDTO.class));

        mockMvc.perform(
                        post("/reviews")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getData());
                    assertNull(response.getErrors());
                });
    }

    @Test
    void testUpdateReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        ReviewRequestDTO request = new ReviewRequestDTO(bookId, "Updated Review", 4);

        doNothing().when(reviewService).updateReview(any(UUID.class), any(ReviewRequestDTO.class));

        mockMvc.perform(
                        patch("/reviews")
                                .header("Authorization", "Bearer valid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getData());
                    assertNull(response.getErrors());
                });
    }

    @Test
    void testGetReviewsForBookSuccessWithoutAuth() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        List<ReviewResponseDTO> reviews = List.of(
                new ReviewResponseDTO(userId, bookId, "Keren", 5, now, now)
        );

        when(reviewService.getReviewsForBook(bookId)).thenReturn(reviews);

        mockMvc.perform(
                        get("/reviews/book/{bookId}", bookId)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<List<ReviewResponseDTO>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertEquals(1, response.getData().size());
                    assertEquals("Keren", response.getData().get(0).getMessage());
                });
    }

    @Test
    void testDeleteReviewSuccess() throws Exception {
        Claims claims = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token")).thenReturn(claims);
        when(jwtUtil.getId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("USER");

        doNothing().when(reviewService).deleteReview(userId, bookId);

        mockMvc.perform(
                        delete("/reviews/{bookId}", bookId)
                                .header("Authorization", "Bearer valid.token")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertEquals("OK", response.getData());
                    assertNull(response.getErrors());
                });
    }


    @Test
    void testUnauthorizedAccessSubmitReview() throws Exception {
        when(jwtUtil.decodeToken("invalid.token")).thenThrow(new JwtException("Invalid token"));

        ReviewRequestDTO request = new ReviewRequestDTO(bookId, "Unauthorized", 3);

        mockMvc.perform(
                        post("/reviews")
                                .header("Authorization", "Bearer invalid.token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<?> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNull(response.getData());
                    assertNotNull(response.getErrors());
                });
    }
}
