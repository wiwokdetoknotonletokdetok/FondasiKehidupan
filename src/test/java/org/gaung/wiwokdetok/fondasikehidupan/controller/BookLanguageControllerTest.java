package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookLanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookLanguageRepository bookLanguageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookLanguageRepository.deleteAll();
        bookLanguageRepository.saveAll(List.of(
                new BookLanguage(0, "English"),
                new BookLanguage(0, "Indonesian"),
                new BookLanguage(0, "Japanese"),
                new BookLanguage(0, "German"),
                new BookLanguage(0, "French")
        ));
    }

    @AfterEach
    void tearDown() {
        bookLanguageRepository.deleteAll();
    }

    @Test
    void testGetLanguages_withKeyword_shouldReturnFilteredLanguages() throws Exception {
        mockMvc.perform(get("/languages")
                        .param("q", "en")
                        .param("limit", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2)) // English & French
                .andDo(result -> {
                    WebResponse<List<String>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getData());
                    assertNull(response.getErrors());
                });
    }

    @Test
    void testGetLanguages_withoutParams_shouldReturnDefaultLimitedLanguages() throws Exception {
        mockMvc.perform(get("/languages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(result -> {
                    WebResponse<List<String>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getData());
                    assertTrue(response.getData().size() <= 5); // karena default limit 5
                    assertNull(response.getErrors());
                });
    }

    @Test
    void testGetLanguages_withNoMatch_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/languages")
                        .param("q", "zzzz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetLanguages_withZeroLimit_shouldUseDefaultLimit() throws Exception {
        mockMvc.perform(get("/languages")
                        .param("limit", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<List<String>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getData());
                    assertNull(response.getErrors());
                });
    }
}
