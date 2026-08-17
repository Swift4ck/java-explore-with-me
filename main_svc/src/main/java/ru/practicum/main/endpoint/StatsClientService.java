package ru.practicum.main.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClientService {

    private final RestTemplate restTemplate;

    @Value("${stats.service.url}")
    private String statsServiceUrl;

    public void sendHit(String app, String uri, String ip) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        try {
            restTemplate.postForObject(statsServiceUrl + "/hit", hitDto, Void.class);
            log.info("Статистика отправлена для URI: {}", uri);
        } catch (Exception e) {
            log.warn("Не удалось отправить статистику в stats-service: {}", e.getMessage());
        }
    }


}
