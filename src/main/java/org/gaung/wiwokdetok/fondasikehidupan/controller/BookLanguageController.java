package org.gaung.wiwokdetok.fondasikehidupan.controller;

import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookLanguageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookLanguageController {

    private final BookLanguageService bookLanguageService;

    public BookLanguageController(BookLanguageService bookLanguageService) {
        this.bookLanguageService = bookLanguageService;
    }

    @GetMapping(
            path = "/languages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<String>>> getBookLanguages(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit) {

        int effectiveLimit = (limit == null || limit <= 0) ? 5 : limit;

        List<String> bookLanguageList = bookLanguageService.searchBookLanguages(q, effectiveLimit);

        WebResponse<List<String>> response = WebResponse.<List<String>>builder()
                .data(bookLanguageList)
                .build();

        return ResponseEntity.ok(response);
    }
}
