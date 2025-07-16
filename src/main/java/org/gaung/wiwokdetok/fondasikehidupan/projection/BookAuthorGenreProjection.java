package org.gaung.wiwokdetok.fondasikehidupan.projection;

import java.util.UUID;

public interface BookAuthorGenreProjection {
    UUID getBookId();
    String getTitle();
    String getIsbn();
    Float getRating();
    String getBookPicture();
    String getPublisherName();
    String getAuthorName();
    String getGenreName();
}
