package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;

import java.util.List;

public interface BookLocationService {

    List<BookLocationResponse> getBookLocations(int id, double latitude, double longitude);

    void addBookLocation(int id, BookLocationRequest request);

    void updateBookLocation(int bookId, int locationId, UpdateBookLocationRequest request);

    void deleteBookLocation(int bookId, int locationId);
}
