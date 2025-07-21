package org.gaung.wiwokdetok.fondasikehidupan.controller;

import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;
import org.gaung.wiwokdetok.fondasikehidupan.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    private List<Genre> mockGenres;

    @BeforeEach
    public void setUp() {
        mockGenres = List.of(new Genre(1, "Fantasy"), new Genre(2, "Science Fiction"), new Genre(3, "Mystery"));
        when(genreService.searchGenres("", 5)).thenReturn(mockGenres);
    }


    @Test
    public void testGetAllGenres() throws Exception {
        mockMvc.perform(get("/genres?q="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(mockGenres.size()))
                .andExpect(jsonPath("$.data[0].genreName").value("Fantasy"))
                .andExpect(jsonPath("$.data[1].genreName").value("Science Fiction"))
                .andExpect(jsonPath("$.data[2].genreName").value("Mystery"));
    }
}