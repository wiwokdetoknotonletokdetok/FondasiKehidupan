package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookPublisherImpl implements BookPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNewBookMessage(String message) {
        rabbitTemplate.convertAndSend("book.exchange", "book.added", message);
    }

    public void sendUpdateBookMessage(String message) {
        rabbitTemplate.convertAndSend("book.exchange", "book.updated", message);
    }

    public void sendDeleteBookMessage(String message) {
        rabbitTemplate.convertAndSend("book.exchange", "book.deleted", message);
    }
}
