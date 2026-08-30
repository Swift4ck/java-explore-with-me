package ru.practicum.main.event.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.event.model.Event;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiator(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiator(Long userId, Long eventId);

    List<Event> findAllByState(EventState state, Pageable pageable);

    List<Event> findAllByState(EventState state);

    boolean existsByCategoryId(Long categoryId);

    List<Event> findByInitiatorIn(List<Long> id);
}


