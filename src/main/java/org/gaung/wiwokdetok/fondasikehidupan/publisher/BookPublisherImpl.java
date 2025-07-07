package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.BookAmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.NewBookMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookPublisherImpl implements BookPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNewBookMessage(NewBookMessage message) {
        rabbitTemplate.convertAndSend(
                BookAmqpConfig.EXCHANGE_NAME,
                BookAmqpConfig.ROUTING_KEY_BOOK_ADDED,
                message
        );
    }

    public void sendUpdateBookMessage(String message) {
        rabbitTemplate.convertAndSend(
                BookAmqpConfig.EXCHANGE_NAME,
                BookAmqpConfig.ROUTING_KEY_BOOK_UPDATED,
                message
        );
    }
}
