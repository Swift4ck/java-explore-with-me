package ru.practicum.stats.service.hit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    List<Hit> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
