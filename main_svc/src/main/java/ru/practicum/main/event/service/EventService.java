package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface EventService {

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    public List<EventShortDto> getEvents(Long userId, int from, int size);

    public EventFullDto getFullEventById(Long userId, Long eventId);

    public EventShortDto updateEvent(Long userId, Long eventId, EventShortDto eventShortDto);

    public List<ParticipationRequestDto> getEventRequestsForUser(Long userId, Long eventId);

    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest requestDto);

}
