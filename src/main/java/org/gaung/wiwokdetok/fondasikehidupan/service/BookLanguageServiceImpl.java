package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookLanguageServiceImpl implements BookLanguageService {

    private final BookLanguageRepository bookLanguageRepository;

    public BookLanguageServiceImpl(BookLanguageRepository bookLanguageRepository) {
        this.bookLanguageRepository = bookLanguageRepository;
    }

    @Override
    public List<String> searchBookLanguages(String keyword, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookLanguageRepository.findLanguagesByKeyword(keyword, pageable);
    }
}
