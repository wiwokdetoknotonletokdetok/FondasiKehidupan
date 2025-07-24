package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
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
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        publisherRepository.deleteAll();

        publisherRepository.saveAll(List.of(
                new Publisher(0, "Gramedia"),
                new Publisher(0, "Erlangga"),
                new Publisher(0, "Mizan")
        ));
    }

    @AfterEach
    void tearDown() {
        publisherRepository.deleteAll();
    }

    @Test
    void testGetPublishers_withKeywordAndLimit_shouldReturnFilteredPublishers() throws Exception {
        mockMvc.perform(get("/publishers")
                        .param("q", "er")
                        .param("limit", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
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
    void testGetPublishers_withoutParams() throws Exception {
        mockMvc.perform(get("/publishers")
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

    @Test
    void testGetPublishers_withNegativeLimit_shouldFallbackToDefaultLimit() throws Exception {
        mockMvc.perform(get("/publishers")
                        .param("limit", "-10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(result -> {
                    WebResponse<List<String>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );

                    assertNotNull(response.getData());
                    assertTrue(response.getData().size() <= 5); // default limit = 5
                    assertNull(response.getErrors());
                });
    }

    @Test
    void testGetPublishers_withZeroLimit_shouldFallbackToDefaultLimit() throws Exception {
        mockMvc.perform(get("/publishers")
                        .param("limit", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
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
