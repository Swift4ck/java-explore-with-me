package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.UpdateEventAdminRequest;
import ru.practicum.main.event.model.UpdateEventUserRequest;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    public List<EventShortDto> getEvents(Long userId, int from, int size);

    public EventFullDto getFullEventById(Long userId, Long eventId);

    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    public List<ParticipationRequestDto> getEventRequestsForUser(Long userId, Long eventId);

    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest requestDto);

    public List<EventShortDto> getPublishedEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request
    );

    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request);

    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);


}
