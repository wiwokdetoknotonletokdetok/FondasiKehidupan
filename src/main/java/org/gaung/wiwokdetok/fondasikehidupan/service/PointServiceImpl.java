package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.config.RabbitMQConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPointMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointServiceImpl implements PointService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void addPoints(String userId, int points) {
        UserPointMessage message = new UserPointMessage(userId, points);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOK_EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USER_POINTS,
                message
        );
    }
}
