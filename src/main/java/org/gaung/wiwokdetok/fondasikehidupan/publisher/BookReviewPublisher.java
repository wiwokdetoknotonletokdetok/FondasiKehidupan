package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookReviewMessage;

public interface BookReviewPublisher {

    void sendNewBookReviewMessage(AmqpBookReviewMessage message);
}
