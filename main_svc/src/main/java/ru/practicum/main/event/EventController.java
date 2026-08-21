package ru.practicum.main.event;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.UpdateEventAdminRequest;
import ru.practicum.main.event.model.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class EventController {

    private final EventService eventService;


    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId, @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {

//        authorizationVerification(userId);

        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {

//        authorizationVerification(userId);

        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullEventById(@PathVariable Long userId, @PathVariable Long eventId) {

//        authorizationVerification(userId);

        return eventService.getFullEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventShortDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                     @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {

//        authorizationVerification(userId);

        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsForUser(@PathVariable Long userId, @PathVariable Long eventId) {

//        authorizationVerification(userId);

        return eventService.getEventRequestsForUser(userId, eventId);
    }




//    public void authorizationVerification(Long userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
//
//        if (!(principal instanceof User)) {
//            throw new ForbiddenException("Пользователь не авторизован корректно");
//        }
//
//        User currentUser = (User) principal;
//        Long currentUserId = currentUser.getId();
//
//        if (!userId.equals(currentUserId)) {
//            throw new ForbiddenException("Нельзя создавать события от чужого имени авторизованы как пользователь:"
//                    + currentUserId + " но пытаетесь действовать от имени: " + userId
//            );
//        }
//    }


    @GetMapping("/events")
    public List<EventShortDto> getPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, formatter) : null;
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, formatter) : null;

        return eventService.getPublishedEvents(text, categories, paid, start, end, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getPublishedEventById(id, request);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }


    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null && !rangeStart.isBlank()) {
            try {
                start = LocalDateTime.parse(rangeStart, formatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Некорректный формат даты rangeStart: " + rangeStart);
            }
        }
        if (rangeEnd != null && !rangeEnd.isBlank()) {
            try {
                end = LocalDateTime.parse(rangeEnd, formatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Некорректный формат даты rangeEnd: " + rangeEnd);
            }
        }

        return eventService.getAdminEvents(users, states, categories, start, end, from, size);
    }


}
