package org.gaung.wiwokdetok.fondasikehidupan.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPrincipal;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.HavingUserBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class HavingUserBookController {

    private final HavingUserBookService service;

    @PostMapping("/me/books/{bookId}")
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<String>> addBookToUser(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId){

        service.addBookToUser(user.getId(), bookId);

        return ResponseEntity.ok(WebResponse.<String>builder()
                .data("Buku berhasil ditambahkan ke koleksi")
                .build());
    }

    @DeleteMapping("/me/books/{bookId}")
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<String>> removeBookFromCollection(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId) {

        service.removeBookFromUserCollection(user.getId(), bookId);
  
        return ResponseEntity.ok(WebResponse.<String>builder()
                .data("Buku berhasil dihapus dari koleksi")
                .build());
    }

    @GetMapping("/{userId}/books")
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> getUserBookCollection(
            @PathVariable UUID userId) {

        List<BookSummaryDTO> books = service.getUserBookCollection(userId);

        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(books)
                .build());
    }

    @GetMapping("/{userId}/books/count")
    @AllowedRoles({"USER"})
    public ResponseEntity<WebResponse<Integer>> countUserBooks(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(WebResponse.<Integer>builder()
                .data(service.getTotalBookCollection(userId))
                .build());
    }
}
