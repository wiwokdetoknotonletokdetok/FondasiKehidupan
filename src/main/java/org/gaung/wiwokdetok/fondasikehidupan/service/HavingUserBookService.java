package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface HavingUserBookService {

    void addBookToUser(UUID userId, UUID bookId);

    List<BookSummaryDTO> getUserBookCollection(UUID userId);

    void removeBookFromUserCollection(UUID userId, UUID bookId);

    int getTotalBookCollection(UUID userId);
}
