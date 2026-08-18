package ru.practicum.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.service.hit.Hit;
import ru.practicum.stats.service.hit.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private HitRepository hitRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void saveHitShouldConvertAndSaveHit() {
        EndpointHit dto = new EndpointHit();
        dto.setApp("ewm-main-service");
        dto.setUri("/events/1");
        dto.setIp("192.168.1.1");
        dto.setTimestamp("2024-01-01 12:00:00");

        statsService.saveHit(dto);

        verify(hitRepository, times(1)).save(any(Hit.class));
    }

    @Test
    public void getStatusShouldReturnViewStatsWithUniqueIps() {
        Hit hit1 = new Hit();
        hit1.setApp("ewm-main-service");
        hit1.setUri("/events/1");
        hit1.setIp("192.168.1.1");
        hit1.setTimestamp(LocalDateTime.parse("2024-01-01 12:00:00", formatter));

        Hit hit2 = new Hit();
        hit2.setApp("ewm-main-service");
        hit2.setUri("/events/1");
        hit2.setIp("192.168.1.2");
        hit2.setTimestamp(LocalDateTime.parse("2024-01-01 13:00:00", formatter));

        when(hitRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of(hit1, hit2));

        var result = statsService.getStatus(
                "2024-01-01 00:00:00",
                "2024-01-02 00:00:00",
                List.of("/events/1"),
                true
        );

        assertEquals(1, result.size());
        assertEquals("ewm-main-service", result.get(0).getApp());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(2, result.get(0).getHits());
    }
}