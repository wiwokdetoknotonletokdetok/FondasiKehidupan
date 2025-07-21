package org.gaung.wiwokdetok.fondasikehidupan.service;

import java.util.List;

public interface PublisherService {

    List<String> searchPublishers(String keyword, int limit);
}
