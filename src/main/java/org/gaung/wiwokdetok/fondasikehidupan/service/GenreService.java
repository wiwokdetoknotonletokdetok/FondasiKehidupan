package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.model.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> searchGenres(String keyword, int limit);
}
