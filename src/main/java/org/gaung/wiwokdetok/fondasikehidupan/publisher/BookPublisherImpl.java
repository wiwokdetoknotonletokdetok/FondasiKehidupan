package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookPublisherImpl implements BookPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendBookMessage(AmqpBookMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_BOOK_ADDED,
                message
        );
    }
}
