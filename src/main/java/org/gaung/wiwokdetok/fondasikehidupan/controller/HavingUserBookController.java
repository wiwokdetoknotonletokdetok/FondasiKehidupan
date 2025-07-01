package org.gaung.wiwokdetok.fondasikehidupan.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.service.HavingUserBookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class HavingUserBookController {

    private final HavingUserBookService service;

    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<Void> addBookToUser(@PathVariable UUID userId, @PathVariable Long bookId){
        service.addBookToUser(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userId}/books")
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> getUserBookCollection(@PathVariable UUID userId) {
        List<BookSummaryDTO> books = service.getUserBookCollection(userId);

        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(books)
                .build());
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromCollection(@PathVariable UUID userId, @PathVariable Long bookId) {
        service.removeBookFromUserCollection(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/books/count")
    public ResponseEntity<WebResponse<Integer>> countUserBooks(@PathVariable UUID userId) {
        return ResponseEntity.ok(WebResponse.<Integer>builder()
                .data(service.getTotalBookCollection(userId))
                .build());
    }
}
