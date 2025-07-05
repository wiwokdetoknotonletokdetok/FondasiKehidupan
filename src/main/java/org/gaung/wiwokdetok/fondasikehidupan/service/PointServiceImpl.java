package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
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

    private final RestTemplate restTemplate;
    @Override
    public void addPoints(String token, int points) {
        String url = "https://wiwokdetok.gaung.org/users/me/points";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Integer> body = Map.of("points", points);
        HttpEntity<Map<String, Integer>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            System.err.println("Gagal menambahkan poin user: " + e.getMessage());
        }
    }
}
