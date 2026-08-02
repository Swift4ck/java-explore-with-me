package ru.practicum.stats.client;

import jakarta.websocket.Endpoint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class StatsClient {

    @Value("${server.url}")
    private String url;

    private final RestTemplate restTemplate;

    public void sendHit(EndpointHit hit) {
        log.info("Запрос на отправку записи {} + {}", hit.getApp(), hit.getUri());
        restTemplate.postForEntity(url + "/hit", hit, Void.class);
    }


    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        log.info("Клиент запрашивает статистику: с {} по {}, unique={}", start, end, unique);

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(url).append("/stats?");

        String encodedStart = start.replace(" ", "%20");
        String encodedEnd = end.replace(" ", "%20");


        urlBuilder.append("start=").append(encodedStart)
                .append("&end=").append(encodedEnd)
                .append("&unique=").append(unique);


        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                String encodedUri = uri.replace(" ", "%20");
                urlBuilder.append("&uris=").append(encodedUri);
            }
        }

        String fullUrl = urlBuilder.toString();

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {
                }
        );

        return response.getBody();

    }


}
