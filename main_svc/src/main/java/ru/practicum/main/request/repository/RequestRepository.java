package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.Status;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;


@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    ParticipationRequest findParticipationRequestByEventIdAndRequesterId(Long eventId, Long requesterId);

    public List<ParticipationRequest> findParticipationRequestByRequesterId(Long userId);

    public Integer countByEventIdAndStatus(Long eventId, Status status);

    public List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, Status status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

}
