package org.gaung.wiwokdetok.fondasikehidupan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPrincipal;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(
            path = "/books/{bookId}/reviews",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<ReviewResponseDTO>>> getReviewsForBook(
            @PathVariable UUID bookId) {

        List<ReviewResponseDTO> reviews = reviewService.getReviewsForBook(bookId);

        WebResponse<List<ReviewResponseDTO>> response = WebResponse.<List<ReviewResponseDTO>>builder()
                .data(reviews)
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/books/{bookId}/reviews",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> submitReview(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId,
            @Valid @RequestBody ReviewRequestDTO dto) {

        reviewService.submitReview(user.getId(), bookId, dto);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("Created")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/books/{bookId}/reviews/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateReview(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateReviewRequestDTO request) {

        reviewService.updateReview(user.getId(), userId, bookId, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @DeleteMapping(
            path = "/books/{bookId}/reviews/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteReview(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId,
            @PathVariable UUID userId) {

        reviewService.deleteReview(user.getId(), userId, bookId);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}
