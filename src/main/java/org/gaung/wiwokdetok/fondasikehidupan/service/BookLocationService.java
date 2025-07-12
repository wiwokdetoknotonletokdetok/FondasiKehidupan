package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;

import java.util.List;
import java.util.UUID;

public interface BookLocationService {

    List<BookLocationResponse> getBookLocations(UUID bookId, double latitude, double longitude);

    void addBookLocation(UUID bookId, BookLocationRequest request);

    void updateBookLocation(UUID bookId, int locationId, UpdateBookLocationRequest request);

    void deleteBookLocation(UUID bookId, int locationId);
}
