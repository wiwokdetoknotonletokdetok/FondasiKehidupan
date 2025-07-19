package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    public static final String EXCHANGE_NAME = "wiwokdetok.exchange";

    public static final String QUEUE_BOOK = "pustakacerdas.book.queue";

    public static final String QUEUE_USER_ACTIVITY = "pustakacerdas.user.activity.queue";

    public static final String QUEUE_USER_POINTS = "kapsulkeaslian.user.points.queue";

    public static final String ROUTING_KEY_BOOK_CREATED = "book.created";

    public static final String ROUTING_KEY_USER_POINTS_BOOK = "user.points.book";

    public static final String ROUTING_KEY_USER_POINTS_LOCATION = "user.points.location";

    public static final String ROUTING_KEY_USER_POINTS_REVIEW = "user.points.review";

    public static final String ROUTING_KEY_USER_ACTIVITY_BOOK_VIEW = "user.activity.book.view";

    private Queue createQueue(String name) {
        return QueueBuilder.durable(name).build();
    }

    private Binding bindQueue(Queue queue, TopicExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue bookQueue() {
        return createQueue(QUEUE_BOOK);
    }

    @Bean
    public Queue userPointsQueue() {
        return createQueue(QUEUE_USER_POINTS);
    }

    @Bean
    public Queue userActivityQueue() {
        return createQueue(QUEUE_USER_ACTIVITY);
    }

    @Bean
    public Binding bindingBookQueue(Queue bookQueue, TopicExchange exchange) {
        return bindQueue(bookQueue, exchange, ROUTING_KEY_BOOK_CREATED);
    }

    @Bean
    public Binding bindingUserPointsBook(Queue userPointsQueue, TopicExchange exchange) {
        return bindQueue(userPointsQueue, exchange, ROUTING_KEY_USER_POINTS_BOOK);
    }

    @Bean
    public Binding bindingUserPointsLocation(Queue userPointsQueue, TopicExchange exchange) {
        return bindQueue(userPointsQueue, exchange, ROUTING_KEY_USER_POINTS_LOCATION);
    }

    @Bean
    public Binding bindingUserPointsReview(Queue userPointsQueue, TopicExchange exchange) {
        return bindQueue(userPointsQueue, exchange, ROUTING_KEY_USER_POINTS_REVIEW);
    }

    @Bean
    public Binding bindingUserActivityBookView(Queue userActivityQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userActivityQueue).to(exchange).with(ROUTING_KEY_USER_ACTIVITY_BOOK_VIEW);
    }
}
