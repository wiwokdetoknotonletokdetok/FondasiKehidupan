package org.gaung.wiwokdetok.fondasikehidupan.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.ReviewResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
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
    public ResponseEntity<WebResponse<ReviewResponseDTO>> submitReview(
            @RequestBody ReviewRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                WebResponse.<ReviewResponseDTO>builder().data(reviewService.submitReview(dto)).build()
        );
    }

    @PatchMapping
    public ResponseEntity<ReviewResponseDTO> updateReview(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.updateReview(dto));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<WebResponse<List<ReviewResponseDTO>>> getReviewsForBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(
                WebResponse.<List<ReviewResponseDTO>>builder()
                        .data(reviewService.getReviewsForBook(bookId))
                        .build()
        );
    }
}