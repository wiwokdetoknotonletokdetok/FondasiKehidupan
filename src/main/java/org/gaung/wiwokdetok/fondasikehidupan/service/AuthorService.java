package org.gaung.wiwokdetok.fondasikehidupan.service;

import java.util.List;

public interface AuthorService {

    List<String> searchAuthors(String keyword, int limit);
}
