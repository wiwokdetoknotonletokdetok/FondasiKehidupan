package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "book.exchange";

    public static final String QUEUE_BOOK_ADDED = "book.added";

    public static final String ROUTING_KEY_BOOK_ADDED = "book.added";

    public static final String QUEUE_BOOK_UPDATED = "book.updated";

    public static final String ROUTING_KEY_BOOK_UPDATED = "book.updated";

    public static final String QUEUE_USER_POINTS = "user.points";
    public static final String ROUTING_KEY_USER_POINTS = "user.points";

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue bookAddedQueue() {
        return new Queue(QUEUE_BOOK_ADDED);
    }

    @Bean
    public Binding bookAddedBinding(Queue bookAddedQueue, TopicExchange bookExchange) {
        return BindingBuilder
                .bind(bookAddedQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_BOOK_ADDED);
    }

    @Bean
    public Queue bookUpdatedQueue() {
        return new Queue(QUEUE_BOOK_UPDATED);
    }

    @Bean
    public Binding bookUpdatedBinding(Queue bookUpdatedQueue, TopicExchange bookExchange) {
        return BindingBuilder
                .bind(bookUpdatedQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_BOOK_UPDATED);
    }
}
