package org.gaung.wiwokdetok.fondasikehidupan.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPrincipal;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.fondasikehidupan.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.fondasikehidupan.service.BookLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class BookLocationController {

    private final BookLocationService bookLocationService;

    public BookLocationController(BookLocationService bookLocationService) {
        this.bookLocationService = bookLocationService;
    }

    @GetMapping(
            path = "/books/{bookId}/locations",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<BookLocationResponse>>> getBookLocations(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @PathVariable UUID bookId) {

        List<BookLocationResponse> bookLocations = bookLocationService.getBookLocations(bookId, latitude, longitude);

        WebResponse<List<BookLocationResponse>> response = WebResponse.<List<BookLocationResponse>>builder()
                .data(bookLocations)
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/books/{bookId}/locations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> addBookLocation(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID bookId,
            @Valid @RequestBody BookLocationRequest request) {

        bookLocationService.addBookLocation(user.getId(), bookId, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("Created")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/books/{bookId}/locations/{locationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateBookLocation(
            @PathVariable UUID bookId,
            @PathVariable Integer locationId,
            @Valid @RequestBody UpdateBookLocationRequest request) {

        bookLocationService.updateBookLocation(bookId, locationId, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowedRoles({"USER"})
    @DeleteMapping(
            path = "/books/{bookId}/locations/{locationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteBookLocation(
            @PathVariable UUID bookId,
            @PathVariable Integer locationId) {

        bookLocationService.deleteBookLocation(bookId, locationId);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
