package ru.practicum.main.event;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.user.model.User;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class EventController {

    private final EventService eventService;


    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId, @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {

        authorizationVerification(userId);

        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody NewEventDto newEventDto) {

        authorizationVerification(userId);

        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullEventById(Long userId, Long eventId) {

        authorizationVerification(userId);

        return eventService.getFullEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventShortDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody EventShortDto eventShortDto) {

        authorizationVerification(userId);

        return eventService.updateEvent(userId, eventId, eventShortDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsForUser(@PathVariable Long userId, @PathVariable Long eventId) {

        authorizationVerification(userId);

        return eventService.getEventRequestsForUser(userId, eventId);
    }

    public void authorizationVerification(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new ForbiddenException("Пользователь не авторизован корректно");
        }

        User currentUser = (User) principal;
        Long currentUserId = currentUser.getId();

        if (!userId.equals(currentUserId)) {
            throw new ForbiddenException("Нельзя создавать события от чужого имени авторизованы как пользователь:"
                    + currentUserId + " но пытаетесь действовать от имени: " + userId
            );
        }
    }


}
