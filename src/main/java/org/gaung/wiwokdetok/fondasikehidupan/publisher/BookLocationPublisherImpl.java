package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpBookConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookLocationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookLocationPublisherImpl implements BookLocationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookLocationPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNewBookLocationMessage(AmqpBookLocationMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpBookConfig.EXCHANGE_NAME,
                "",
                message
        );
    }
}
