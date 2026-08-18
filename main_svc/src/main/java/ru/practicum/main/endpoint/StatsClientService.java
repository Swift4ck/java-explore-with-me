package ru.practicum.main.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Long, Long> getViews(List<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        String start = "1970-01-01 00:00:00";
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String url = statsServiceUrl + "/stats?start=" + encode(start) + "&end=" + encode(end) + "&unique=false";
        for (String uri : uris) {
            url += "&uris=" + encode(uri);
        }

        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {}
        );

        List<ViewStatsDto> stats = response.getBody();
        if (stats == null) {
            return Collections.emptyMap();
        }

        return stats.stream()
                .filter(vs -> vs.getUri().startsWith("/events/"))
                .collect(Collectors.toMap(
                        vs -> Long.parseLong(vs.getUri().substring("/events/".length())),
                        ViewStatsDto::getHits,
                        (a, b) -> a
                ));
    }

    private String encode(String value) {
        return value.replace(" ", "%20");
    }

}
