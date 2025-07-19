package org.gaung.wiwokdetok.fondasikehidupan.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.BookPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookPictureServiceImpl implements BookPictureService {

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-endpoint}")
    private String publicEndpoint;

    private final AmazonS3 amazonS3;

    private final BookRepository bookRepository;

    private final BookPublisher bookPublisher;

    @Override
    public String uploadBookPicture(UUID bookId, MultipartFile file) {
        String fileName = String.format("books/%s.jpg", bookId);
        String version = String.valueOf(System.currentTimeMillis());

        try {
            ByteArrayInputStream resizedImageStream = resizeAndCropImage(file);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(resizedImageStream.available());
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileName, resizedImageStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            String bookPictureUrl = String.format("%s/%s?v=%s", publicEndpoint, fileName, version);

            saveBookPicture(bookId, bookPictureUrl);

            return bookPictureUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal upload book picture");
        }
    }

    private ByteArrayInputStream resizeAndCropImage(MultipartFile file) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .crop(Positions.CENTER)
                    .size(300, 450)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private void saveBookPicture(UUID bookId, String bookPictureUrl) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        book.setBookPicture(bookPictureUrl);
        sendBookPictureAddedMessage(book);
        bookRepository.save(book);
    }

    private void sendBookPictureAddedMessage(Book book) {
        AmqpBookMessage message = new AmqpBookMessage();
        message.setId(book.getId());
        message.setTitle(book.getTitle());
        message.setSynopsis(book.getSynopsis());
        message.setBookPicture(book.getBookPicture());
        message.setCreatedBy(book.getCreatedBy());
        bookPublisher.sendBookPictureAddedMessage(message);
    }
}
