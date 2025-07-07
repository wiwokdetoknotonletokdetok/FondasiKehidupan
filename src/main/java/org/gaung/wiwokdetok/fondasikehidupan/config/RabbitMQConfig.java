package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOK_EXCHANGE_NAME = "book.exchange";

    public static final String QUEUE_BOOK_ADDED = "book.added";
    public static final String ROUTING_KEY_BOOK_ADDED = "book.added";

    public static final String QUEUE_BOOK_UPDATED = "book.updated";
    public static final String ROUTING_KEY_BOOK_UPDATED = "book.updated";

    public static final String REVIEW_EXCHANGE_NAME = "review.exchange";

    public static final String QUEUE_REVIEW_ADDED = "review.added";
    public static final String ROUTING_KEY_REVIEW_ADDED = "review.added";

    public static final String QUEUE_REVIEW_UPDATED = "review.updated";
    public static final String ROUTING_KEY_REVIEW_UPDATED = "review.updated";

    public static final String QUEUE_USER_POINTS = "user.points";
    public static final String ROUTING_KEY_USER_POINTS = "user.points";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(BOOK_EXCHANGE_NAME, true, false);
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

    @Bean
    public Queue userPointsQueue() {
        return new Queue(QUEUE_USER_POINTS, true);
    }

    @Bean
    public Binding userPointsBinding(Queue userPointsQueue, TopicExchange bookExchange) {
        return BindingBuilder
                .bind(userPointsQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_USER_POINTS);
    }

    @Bean
    public TopicExchange reviewExchange() {
        return new TopicExchange(REVIEW_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue reviewAddedQueue() {
        return new Queue(QUEUE_REVIEW_ADDED, true);
    }

    @Bean
    public Binding reviewAddedBinding(Queue reviewAddedQueue, TopicExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewAddedQueue)
                .to(reviewExchange)
                .with(ROUTING_KEY_REVIEW_ADDED);
    }

    @Bean
    public Queue reviewUpdatedQueue() {
        return new Queue(QUEUE_REVIEW_UPDATED, true);
    }

    @Bean
    public Binding reviewUpdatedBinding(Queue reviewUpdatedQueue, TopicExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewUpdatedQueue)
                .to(reviewExchange)
                .with(ROUTING_KEY_REVIEW_UPDATED);
    }
}
