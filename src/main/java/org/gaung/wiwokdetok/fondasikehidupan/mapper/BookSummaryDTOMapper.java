package org.gaung.wiwokdetok.fondasikehidupan.mapper;

import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.projection.BookAuthorGenreProjection;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookSummaryDTOMapper {
    public List<BookSummaryDTO> groupFromProjections(List<BookAuthorGenreProjection> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        BookAuthorGenreProjection::getBookId,
                        row -> new BookSummaryDTO(row.getBookId(), row.getTitle(), row.getBookPicture()),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .collect(Collectors.toList());
    }
}
