package org.gaung.wiwokdetok.fondasikehidupan.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPrincipal;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<String>> submitReview(@CurrentUser UserPrincipal user, @RequestBody ReviewRequestDTO dto) {
        reviewService.submitReview(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<String>builder().data("OK").build());
    }

    @PatchMapping
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<String>> updateReview(@CurrentUser UserPrincipal user, @RequestBody ReviewRequestDTO dto) {
        reviewService.updateReview(user.getId(), dto);
        return ResponseEntity.ok(WebResponse.<String>builder().data("OK").build());
    }


    @GetMapping("/book/{bookId}")
    public ResponseEntity<WebResponse<List<ReviewResponseDTO>>> getReviewsForBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(
                WebResponse.<List<ReviewResponseDTO>>builder()
                        .data(reviewService.getReviewsForBook(bookId))
                        .build()
        );
    }

    @DeleteMapping("/{bookId}")
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<String>> deleteReview(@CurrentUser UserPrincipal user, @PathVariable Long bookId) {
        reviewService.deleteReview(user.getId(), bookId);
        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}