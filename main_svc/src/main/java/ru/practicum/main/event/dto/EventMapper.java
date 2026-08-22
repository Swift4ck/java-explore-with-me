package ru.practicum.main.event.dto;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;

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
        CategoryDto categoryDto = null;
        if (event.getCategory() != null) {
            categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
        }

        LocationDto locationDto = null;
        if (event.getLocation() != null) {
            locationDto = new LocationDto(
                    event.getLocation().getId(),
                    event.getLocation().getLat(),
                    event.getLocation().getLon()
            );
        }

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(locationDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(event.getViews() != null ? event.getViews() : 0L)
                .build();
    }

    public static EventFullDto toEventFullDtoAndViews(Event event, Long views) {

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(locationDto(event.getLocation()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .category(event.getCategory() != null ?
                        new CategoryDto(event.getCategory().getId(), event.getCategory().getName()) : null)
                .views(views)
                .build();

    }

    public static EventShortDto toEventShortDto(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String eventDate = event.getEventDate() != null ? event.getEventDate().format(formatter) : null;

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(eventDate)
                .id(event.getId())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .category(event.getCategory() != null ?
                        new CategoryDto(event.getCategory().getId(), event.getCategory().getName()) : null)
                .views(event.getViews() != null ? event.getViews() : 0L)
                .build();
    }

    public static EventShortDto toEventShortDtoAndViews(Event event, Long views) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String eventDate = event.getEventDate() != null ? event.getEventDate().format(formatter) : null;

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(eventDate)
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

    public static LocationDto locationDto(Location location) {

        LocationDto locationDto = new LocationDto();

        locationDto.setId(location.getId());
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }


}
