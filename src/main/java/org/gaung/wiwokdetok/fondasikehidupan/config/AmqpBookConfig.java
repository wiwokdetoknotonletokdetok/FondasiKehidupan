package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpBookConfig {

    public static final String EXCHANGE_NAME = "book.exchange";

    public static final String QUEUE_BOOK_ADDED = "book.added";

    public static final String QUEUE_BOOK_UPDATED = "book.updated";

    public static final String QUEUE_BOOK_REVIEW_ADDED = "book.review.added";

    public static final String QUEUE_BOOK_LOCATION_ADDED = "book.location.added";

    private Queue createQueue(String name) {
        return QueueBuilder.durable(name).build();
    }

    private Binding bindQueue(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public FanoutExchange bookExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue bookAddedQueue() {
        return createQueue(QUEUE_BOOK_ADDED);
    }

    @Bean
    public Binding bookAddedBinding(Queue bookAddedQueue, FanoutExchange bookExchange) {
        return bindQueue(bookAddedQueue, bookExchange);
    }

    @Bean
    public Queue bookUpdatedQueue() {
        return createQueue(QUEUE_BOOK_UPDATED);
    }

    @Bean
    public Binding bookUpdatedBinding(Queue bookUpdatedQueue, FanoutExchange bookExchange) {
        return bindQueue(bookUpdatedQueue, bookExchange);
    }

    @Bean
    public Queue bookReviewAddedQueue() {
        return createQueue(QUEUE_BOOK_REVIEW_ADDED);
    }

    @Bean
    public Binding bookReviewAddedBinding(Queue bookReviewAddedQueue, FanoutExchange bookExchange) {
        return bindQueue(bookReviewAddedQueue, bookExchange);
    }

    @Bean
    public Queue bookLocationAddedQueue() {
        return createQueue(QUEUE_BOOK_LOCATION_ADDED);
    }

    @Bean
    public Binding bookLocationAddedBinding(Queue bookLocationAddedQueue, FanoutExchange bookExchange) {
        return bindQueue(bookLocationAddedQueue, bookExchange);
    }
}
