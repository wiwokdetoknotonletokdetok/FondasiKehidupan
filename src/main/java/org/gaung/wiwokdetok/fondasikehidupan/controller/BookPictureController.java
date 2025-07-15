package org.gaung.wiwokdetok.fondasikehidupan.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookPictureRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookPictureService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class BookPictureController {

    private final BookPictureService bookPictureService;

    public BookPictureController(BookPictureService bookPictureService) {
        this.bookPictureService = bookPictureService;
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/books/new/book-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> uploadProfilePicture(
            @Valid @ModelAttribute BookPictureRequest request) {

        String bookPictureUrl = bookPictureService.uploadBookPicture(request.getBookPicture());

        WebResponse<String> response = WebResponse.<String>builder()
                .data("Created")
                .build();

        return ResponseEntity.created(URI.create(bookPictureUrl)).body(response);
    }
}
