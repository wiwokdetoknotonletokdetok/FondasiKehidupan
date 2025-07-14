package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpConfig;
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
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_USER_POINTS_LOCATION,
                message
        );
    }
}
