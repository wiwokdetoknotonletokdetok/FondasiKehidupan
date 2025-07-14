package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserPointsMessage;

public interface UserPointsPublisher {

    void sendUserPointsForBook(AmqpUserPointsMessage message);

    void sendUserPointsForReview(AmqpUserPointsMessage message);

    void sendUserPointsForLocation(AmqpUserPointsMessage message);
}
