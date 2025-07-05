package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.config.RabbitMQConfig;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UserPointMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PointServiceImpl implements PointService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void addPoints(String userId, int points) {
        UserPointMessage message = new UserPointMessage(userId, points);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USER_POINTS,
                message
        );
    }
}
