package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        List<String> mockGenres = List.of("Fantasy", "Science Fiction", "Mystery");
        when(genreRepository.findAllGenreNames()).thenReturn(mockGenres);

        // Act
        List<String> result = genreService.getAllGenreNames();

        // Assert
        assertEquals(mockGenres.size(), result.size());
        assertEquals("Fantasy", result.get(0));
        assertEquals("Science Fiction", result.get(1));
        assertEquals("Mystery", result.get(2));
    }
}
