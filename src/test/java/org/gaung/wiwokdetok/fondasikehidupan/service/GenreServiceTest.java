package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    public void testGetAllGenreNames() {
        List<Genre> mockGenres = List.of(new Genre(1, "Fantasy"), new Genre(2, "Science Fiction"), new Genre(3, "Mystery"));
        Pageable pageable = PageRequest.of(0, 5);
        when(genreRepository.findByGenreNameContainingIgnoreCase("", pageable)).thenReturn(mockGenres);

        List<Genre> result = genreService.searchGenres("", 5);

        assertEquals(mockGenres.size(), result.size());
        assertEquals("Fantasy", result.get(0).getGenreName());
        assertEquals("Science Fiction", result.get(1).getGenreName());
        assertEquals("Mystery", result.get(2).getGenreName());
    }
}
