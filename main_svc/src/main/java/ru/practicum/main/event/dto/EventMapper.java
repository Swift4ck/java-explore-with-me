package ru.practicum.main.event.dto;

import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private CategoryRepository categoryRepository;

    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());

        event.setDescription(newEventDto.getDescription());


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        event.setEventDate(timestamp);

        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setTitle(newEventDto.getTitle());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setConfirmedRequests(0);

        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();

    }

    public static EventFullDto toEventFullDtoAndViews(Event event, Long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(views)
                .build();

    }

    public static EventShortDto toEventShortDto(Event event) {

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().toString())
                .id(event.getId())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDtoAndViews(Event event, Long views) {

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().toString())
                .id(event.getId())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static Event eventShortToEvent(EventShortDto eventShortDto) {

        Event event = new Event();

        event.setAnnotation(eventShortDto.getAnnotation());
        event.setConfirmedRequests(eventShortDto.getConfirmedRequests());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse(eventShortDto.getEventDate(), formatter);
        event.setEventDate(timestamp);

        event.setId(eventShortDto.getId());
        event.setInitiator(eventShortDto.getInitiator());
        event.setPaid(eventShortDto.getPaid());
        event.setTitle(eventShortDto.getTitle());

        return event;
    }


}
