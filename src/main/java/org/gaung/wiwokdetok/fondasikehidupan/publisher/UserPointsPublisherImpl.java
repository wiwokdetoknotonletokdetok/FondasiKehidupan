package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserPointsMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserPointsPublisherImpl implements UserPointsPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserPointsPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserPointsForBook(AmqpUserPointsMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_USER_POINTS_BOOK,
                message
        );
    }

    public void sendUserPointsForLocation(AmqpUserPointsMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_USER_POINTS_LOCATION,
                message
        );
    }

    public void sendUserPointsForReview(AmqpUserPointsMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_USER_POINTS_REVIEW,
                message
        );
    }
}
