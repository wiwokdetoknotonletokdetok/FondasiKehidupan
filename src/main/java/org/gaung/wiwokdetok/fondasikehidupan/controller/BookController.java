package org.gaung.wiwokdetok.fondasikehidupan.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookRequestDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookResponseDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<WebResponse<BookResponseDTO>> createBook(@RequestBody BookRequestDTO dto) {
        BookResponseDTO created = bookService.createBook(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WebResponse.<BookResponseDTO>builder()
                        .data(created)
                        .build());
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> getAllBooks() {
        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(bookService.getAllBooks())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> searchByTitle(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(WebResponse.<List<BookSummaryDTO>>builder()
                .data(bookService.findByTitleContainingIgnoreCase(keyword))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<BookResponseDTO>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(WebResponse.<BookResponseDTO>builder()
                .data(bookService.getBookById(id))
                .build());
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<WebResponse<List<BookSummaryDTO>>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String publisher
    ) {
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
