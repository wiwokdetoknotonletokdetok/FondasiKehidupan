package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewPublisherImpl implements ReviewPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ReviewPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNewReviewMessage(String message) {
        rabbitTemplate.convertAndSend("review.exchange", "review.added", message);
    }

    @Override
    public void sendUpdateReviewMessage(String message) {
        rabbitTemplate.convertAndSend("review.exchange", "review.updated", message);
    }
}
