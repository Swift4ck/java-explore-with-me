package ru.practicum.stats.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.hit.Hit;
import ru.practicum.stats.service.hit.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;


    @Transactional
    @Override
    public void saveHit(EndpointHit dto) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timestamp = LocalDateTime.parse(dto.getTimestamp(), formatter);

        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(timestamp);

        hitRepository.save(hit);
    }


    @Override
    public List<ViewStats> getStatus(String start, String end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startFormat = LocalDateTime.parse(start, formatter);
        LocalDateTime endFormat = LocalDateTime.parse(end, formatter);

        if (startFormat.isAfter(endFormat)) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания");
        }

        List<Hit> hits = hitRepository.findByTimestampBetween(startFormat, endFormat);


        if (uris != null && !uris.isEmpty()) {
            hits = hits.stream()
                    .filter(h -> uris.contains(h.getUri()))
                    .collect(Collectors.toList());
        }

        Map<String, List<Hit>> groupHits = hits.stream()
                .collect(Collectors.groupingBy(hit -> hit.getApp() + "::" + hit.getUri()));

        return groupHits.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    List<Hit> group = entry.getValue();

                    String[] parts = key.split("::");

                    String app = parts[0];
                    String uri = parts[1];

                    long count;

                    if (unique) {
                        count = group.stream()
                                .map(Hit::getIp)
                                .distinct()
                                .count();
                    } else {
                        count = group.size();
                    }

                    ViewStats stats = new ViewStats();
                    stats.setApp(app);
                    stats.setUri(uri);
                    stats.setHits(count);

                    return stats;
                })
                .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                .collect(Collectors.toList());

    }
}


