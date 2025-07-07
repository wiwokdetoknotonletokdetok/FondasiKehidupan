package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.NewReviewMessage;

public interface ReviewPublisher {

    void sendNewReviewMessage(NewReviewMessage message);
}
