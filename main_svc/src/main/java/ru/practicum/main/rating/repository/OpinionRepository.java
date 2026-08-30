package ru.practicum.main.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.enums.Rating;
import ru.practicum.main.rating.model.Opinion;


public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    public boolean existsByUserIdAndEventId(Long userId, Long eventId);

    public Opinion findByUserIdAndEventId(Long userId, Long eventId);

    public Long countByEventIdAndRating(Long eventId, Rating rating);


}
