package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.ReviewAmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.NewReviewMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewPublisherImpl implements ReviewPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ReviewPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNewReviewMessage(NewReviewMessage message) {
        rabbitTemplate.convertAndSend(
                ReviewAmqpConfig.EXCHANGE_NAME,
                "",
                message
        );
    }
}
