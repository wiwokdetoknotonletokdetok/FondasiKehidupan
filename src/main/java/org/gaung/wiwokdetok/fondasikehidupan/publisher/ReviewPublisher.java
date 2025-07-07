package org.gaung.wiwokdetok.fondasikehidupan.publisher;

public interface ReviewPublisher {
    void sendNewReviewMessage(String message);
    void sendUpdateReviewMessage(String message);
}
