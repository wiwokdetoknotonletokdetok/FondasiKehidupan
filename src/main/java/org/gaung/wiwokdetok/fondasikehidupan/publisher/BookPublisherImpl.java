package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.config.AmqpConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookPublisherImpl implements BookPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendBookCreatedMessage(AmqpBookMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_BOOK_CREATED,
                message, msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                });
    }

    @Override
    public void sendBookPictureAddedMessage(AmqpBookMessage message) {
        rabbitTemplate.convertAndSend(
                AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_BOOK_PICTURE_ADDED,
                message, msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                });
    }
}
