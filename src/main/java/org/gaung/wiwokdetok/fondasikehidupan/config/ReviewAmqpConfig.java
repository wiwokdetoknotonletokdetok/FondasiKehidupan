package org.gaung.wiwokdetok.fondasikehidupan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewAmqpConfig {

    public static final String EXCHANGE_NAME = "review.exchange";

    public static final String QUEUE_REVIEW_ADDED = "review.added";

    @Bean
    public FanoutExchange reviewExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue reviewAddedQueue() {
        return new Queue(QUEUE_REVIEW_ADDED, true);
    }

    @Bean
    public Binding reviewAddedBinding(Queue reviewAddedQueue, FanoutExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewAddedQueue)
                .to(reviewExchange);
    }
}
