package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookReviewMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookReviewPublisherImpl implements BookReviewPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookReviewPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNewBookReviewMessage(AmqpBookReviewMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_USER_POINTS_REVIEW,
                message
        );
    }
}
