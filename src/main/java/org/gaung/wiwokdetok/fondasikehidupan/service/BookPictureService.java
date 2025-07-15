package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.springframework.web.multipart.MultipartFile;

public interface BookPictureService {

    String uploadBookPicture(MultipartFile bookPicture);
}
