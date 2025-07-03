package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;

import java.util.List;

public interface BookLocationService {

    List<BookLocationResponse> getBookLocations(long bookId, double latitude, double longitude);

    void addBookLocation(long bookId, BookLocationRequest request);

    void updateBookLocation(long bookId, long locationId, UpdateBookLocationRequest request);

    void deleteBookLocation(long bookId, long locationId);
}
