package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookAmqpConfig {

    public static final String EXCHANGE_NAME = "book.exchange";

    public static final String QUEUE_BOOK_ADDED = "book.added";

    public static final String ROUTING_KEY_BOOK_ADDED = "book.added";

    public static final String QUEUE_BOOK_UPDATED = "book.updated";

    public static final String ROUTING_KEY_BOOK_UPDATED = "book.updated";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue bookAddedQueue() {
        return new Queue(QUEUE_BOOK_ADDED, true);
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
        return new Queue(QUEUE_BOOK_UPDATED, true);
    }

    @Bean
    public Binding bookUpdatedBinding(Queue bookUpdatedQueue, TopicExchange bookExchange) {
        return BindingBuilder
                .bind(bookUpdatedQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_BOOK_UPDATED);
    }
}
