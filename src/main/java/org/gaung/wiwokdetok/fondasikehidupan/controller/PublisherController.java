package org.gaung.wiwokdetok.fondasikehidupan.controller;

import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.service.PublisherService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping(
            path = "/publishers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<String>>> getPublishers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit) {

        int effectiveLimit = (limit == null || limit <= 0) ? 5 : limit;

        List<String> publisherList = publisherService.searchPublishers(q, effectiveLimit);

        WebResponse<List<String>> response = WebResponse.<List<String>>builder()
                .data(publisherList)
                .build();

        return ResponseEntity.ok(response);
    }
}
