package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BookPictureService {

    String uploadBookPicture(UUID bookId, MultipartFile bookPicture);
}
