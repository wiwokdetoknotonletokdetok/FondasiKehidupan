package org.gaung.wiwokdetok.fondasikehidupan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/books",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> createBook(
            @Valid @RequestBody BookRequestDTO dto,
            @Valid @CurrentUser UserPrincipal user) {

        bookService.createBook(dto, user.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse.<String>builder()
                        .data("OK")
                        .build());
    }

    @GetMapping(
            path = "books/{bookId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<BookResponseDTO>> getBookById(
            @PathVariable UUID bookId) {

        return ResponseEntity.ok(WebResponse.<BookResponseDTO>builder()
                .data(bookService.getBookById(bookId))
                .build());
    }

    @GetMapping(
            path = "/books",
            produces = "application/json"
    )
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String publisher) {

        if ("".equals(title)) title = null;
        if ("".equals(isbn)) isbn = null;
        if ("".equals(author)) author = null;
        if ("".equals(genre)) genre = null;
        if ("".equals(publisher)) publisher = null;
        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(bookService.advancedSearch(title, isbn, author, genre, publisher))
                .build());
    }
}
