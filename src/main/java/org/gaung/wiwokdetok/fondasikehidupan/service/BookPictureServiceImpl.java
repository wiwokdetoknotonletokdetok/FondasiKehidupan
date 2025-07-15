package org.gaung.wiwokdetok.fondasikehidupan.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
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
public class BookPictureServiceImpl implements BookPictureService {

    @Value("${CLOUDFLARE_R2_BUCKET_NAME}")
    private String bucketName;

    @Value("${CLOUDFLARE_R2_PUBLIC_ENDPOINT}")
    private String publicEndpoint;

    private final AmazonS3 amazonS3;

    public BookPictureServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadBookPicture(MultipartFile file) {
        String fileName = String.format("books/%s.jpg", UUID.randomUUID());
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

            return String.format("%s/%s?v=%s", publicEndpoint, fileName, version);
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
}
