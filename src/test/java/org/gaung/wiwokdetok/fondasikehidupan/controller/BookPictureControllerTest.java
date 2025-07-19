package org.gaung.wiwokdetok.fondasikehidupan.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLanguage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLanguageRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.PublisherRepository;
import org.gaung.wiwokdetok.fondasikehidupan.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookPictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private BookPublisher bookPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookLanguageRepository bookLanguageRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        Publisher publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);

        BookLanguage language = new BookLanguage();
        language.setLanguage("Indonesia");
        bookLanguageRepository.save(language);

        book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("978-0-306-40615-7");
        book.setSynopsis("Book Synopsis");
        book.setBookPicture("https://example.com");
        book.setPublisher(publisher);
        book.setLanguage(language);
        book.setCreatedBy(UUID.randomUUID());
        bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        publisherRepository.deleteAll();
        bookLanguageRepository.deleteAll();
    }

    private MockMultipartFile generateDummyPicture() throws IOException {
        BufferedImage dummyImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", os);
        byte[] imageBytes = os.toByteArray();

        return new MockMultipartFile(
                "bookPicture",
                "dummy.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageBytes
        );
    }

    @Test
    void testUploadBookPictureSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtUtil.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtUtil.getId(payload)).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRole(payload)).thenReturn("USER");

        MockMultipartFile multipartFile = generateDummyPicture();

        doNothing().when(bookPublisher).sendBookPictureAddedMessage(any(AmqpBookMessage.class));

        when(amazonS3.putObject(any(PutObjectRequest.class)))
                .thenReturn(new PutObjectResult());

        mockMvc.perform(
                multipart("/books/{bookId}/book-picture", book.getId())
                        .file(multipartFile)
                        .header("Authorization", "Bearer valid.token.here")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isCreated(),
                header().exists("Location")
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {}
            );
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testUploadBookPictureFailedWhenTokenIsInvalid() throws Exception {
        when(jwtUtil.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        MockMultipartFile multipartFile = generateDummyPicture();

        mockMvc.perform(
                multipart("/books/{bookId}/book-picture", book.getId())
                        .file(multipartFile)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
