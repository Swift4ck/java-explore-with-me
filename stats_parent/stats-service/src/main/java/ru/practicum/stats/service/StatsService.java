package ru.practicum.stats.service;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.hit.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    public void saveHit(EndpointHit dto);

    public List<ViewStats> getStatus(String start, String end, List<String> uris, boolean unique);
}
