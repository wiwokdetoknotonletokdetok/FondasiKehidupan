package org.gaung.wiwokdetok.fondasikehidupan.mapper;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookSummaryDTO;
import org.gaung.wiwokdetok.fondasikehidupan.projection.BookAuthorGenreProjection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookSummaryDTOMapper {
    public static List<BookSummaryDTO> groupFromProjections(List<BookAuthorGenreProjection> rows) {
        Map<UUID, BookSummaryDTO> grouped = new LinkedHashMap<>();

        for (BookAuthorGenreProjection row : rows) {
            grouped.computeIfAbsent(row.getBookId(), id -> new BookSummaryDTO(
                    id,
                    row.getTitle(),
                    row.getIsbn(),
                    row.getRating(),
                    row.getBookPicture(),
                    row.getPublisherName(),
                    new ArrayList<>(),
                    new ArrayList<>()
            ));

            BookSummaryDTO dto = grouped.get(row.getBookId());

            if (!dto.getAuthorNames().contains(row.getAuthorName())) {
                dto.getAuthorNames().add(row.getAuthorName());
            }

            if (!dto.getGenreNames().contains(row.getGenreName())) {
                dto.getGenreNames().add(row.getGenreName());
            }
        }

        return new ArrayList<>(grouped.values());
    }
}
