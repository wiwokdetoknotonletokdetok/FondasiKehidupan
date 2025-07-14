package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpBookConfig;
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
    public void sendNewBookMessage(AmqpBookMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpBookConfig.EXCHANGE_NAME,
                "",
                message
        );
    }

    @Override
    public void sendUpdateBookMessage(AmqpBookMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpBookConfig.EXCHANGE_NAME,
                "",
                message
        );
    }
}
