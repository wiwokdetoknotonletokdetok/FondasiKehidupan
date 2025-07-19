package org.gaung.wiwokdetok.fondasikehidupan.controller;

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

    private List<String> mockGenres;

    @BeforeEach
    public void setUp() {
        mockGenres = List.of("Fantasy", "Science Fiction", "Mystery");
        when(genreService.getAllGenreNames()).thenReturn(mockGenres);
    }


    @Test
    public void testGetAllGenres() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockGenres.size()))
                .andExpect(jsonPath("$[0]").value("Fantasy"))
                .andExpect(jsonPath("$[1]").value("Science Fiction"))
                .andExpect(jsonPath("$[2]").value("Mystery"));
    }
}