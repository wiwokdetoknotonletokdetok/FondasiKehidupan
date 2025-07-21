package org.gaung.wiwokdetok.fondasikehidupan.controller;

import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.service.AuthorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(
            path = "/authors",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<String>>> getAuthorNames(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit) {

        int effectiveLimit = (limit == null || limit <= 0) ? 5 : limit;

        List<String> authorList = authorService.searchAuthors(q, effectiveLimit);

        WebResponse<List<String>> response = WebResponse.<List<String>>builder()
                .data(authorList)
                .build();

        return ResponseEntity.ok(response);
    }
}
