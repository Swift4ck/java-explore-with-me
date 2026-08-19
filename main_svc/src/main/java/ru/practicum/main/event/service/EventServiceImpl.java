package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.endpoint.StatsClientService;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.Status;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventMapper;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.ParticipationMapper;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClientService statsClientService;
    private final CategoryRepository categoryRepository;


    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (userId == null || newEventDto == null) {
            throw new BadRequestException("Данные не корректные:" + userId + newEventDto);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timestamp = LocalDateTime.parse(newEventDto.getEventDate(), formatter);

        if (timestamp.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента: "
                    + LocalDateTime.now().plusHours(2));
        }

        Event newEvent = EventMapper.toEvent(newEventDto);

        newEvent.setCategory(categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID " + newEventDto.getCategory() + " не найдено")));


        newEvent.setInitiator(userId);
        newEvent.setState(EventState.PENDING);

        Event saveEvent = eventRepository.save(newEvent);

        return EventMapper.toEventFullDto(saveEvent);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.info("Получен запрос на получение мероприятий пользователя {}", userId);

        PageRequest pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findByInitiator(userId, pageable);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = statsClientService.getViews(eventIds);


        return events.stream()
                .map(event -> EventMapper.toEventShortDtoAndViews(event, viewsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto getFullEventById(Long userId, Long eventId) {
        log.info("Получен запрос на полную информацию мероприятия от пользователя:{} для мероприятия{}", userId, eventId);

        Event getEvent = eventRepository.findByIdAndInitiator(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        return EventMapper.toEventFullDto(getEvent);
    }

    @Override
    @Transactional
    public EventShortDto updateEvent(Long userId, Long eventId, EventShortDto eventShortDto) {
        log.info("Получен запрос на изменение мероприятия от пользователя:{} для мероприятия{}", userId, eventId);


        Event updateEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        if (eventShortDto == null) {
            throw new BadRequestException("Данные не корректны");
        }

        checkIsEmpty(eventShortDto, updateEvent);

        return EventMapper.toEventShortDto(updateEvent);
    }


    @Override
    public List<ParticipationRequestDto> getEventRequestsForUser(Long userId, Long eventId) {
        log.info("Запрос на получение информации о запросах текущего пользователя {}, на событии {}",
                userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        List<ParticipationRequest> participationRequestList = new ArrayList<>();

        participationRequestList.add(requestRepository.findParticipationRequestByEventIdAndRequesterId(eventId, userId));

        return participationRequestList.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .toList();
    }


    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest requestDto) {
        log.info("Запрос на изменение статуса заявок. Пользователь: {}, Событие: {}", userId, eventId);

        if (requestDto == null || requestDto.getRequestIds() == null || requestDto.getRequestIds().isEmpty()) {
            throw new BadRequestException("Запрос составлен некорректно: список ID заявок пуст или отсутствует");
        }

        Status newStatus = requestDto.getStatus();
        if (newStatus == null || (newStatus != Status.CONFIRMED && newStatus != Status.REJECTED)) {
            throw new BadRequestException("Некорректный статус заявки. Допустимо только CONFIRMED или REJECTED");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException("Событие не найдено или недоступно");
        }


        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Для данного события подтверждение заявок не требуется");
        }

        if (newStatus == Status.CONFIRMED) {
            long currentConfirmed = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (currentConfirmed >= event.getParticipantLimit()) {

                for (Long requestId : requestDto.getRequestIds()) {
                    ParticipationRequest participationRequest = requestRepository.findById(requestId)
                            .orElseThrow(()
                                    -> new NotFoundException("Не найдена заявка на участие в событии:" + requestId));

                    participationRequest.setStatus(Status.REJECTED);
                    requestRepository.save(participationRequest);
                }
                throw new ConflictException("Достигнут лимит одобренных заявок");
            }
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        int actualLimit = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
        int countCycle = 0;

        for (Long requestId : requestDto.getRequestIds()) {

            if (actualLimit + countCycle < event.getParticipantLimit() && requestDto.getStatus().equals(Status.CONFIRMED)) {
                ParticipationRequest participationRequest = requestRepository.findById(requestId)
                        .orElseThrow(()
                                -> new NotFoundException("Не найдена заявка на участие в событии:" + requestId));

                if (!participationRequest.getStatus().equals(Status.PENDING)) {
                    throw new BadRequestException("Подтверждать статус можно только заявки со статусом PENDING");
                }

                confirmed.add(ParticipationMapper.toParticipationRequestDto(participationRequest));
                countCycle++;
            } else {
                ParticipationRequest participationRequest = requestRepository.findById(requestId)
                        .orElseThrow(()
                                -> new NotFoundException("Не найдена заявка на участие в событии:" + requestId));

                rejected.add(ParticipationMapper.toParticipationRequestDto(participationRequest));
            }

        }

        for (ParticipationRequestDto dto : confirmed) {
            dto.setStatus(requestDto.getStatus());
            requestRepository.save(ParticipationMapper.toParticipationRequest(dto));
        }

        for (ParticipationRequestDto dto : rejected) {
            dto.setStatus(Status.REJECTED);
            requestRepository.save(ParticipationMapper.toParticipationRequest(dto));
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmed, rejected);
        return result;
    }


    @Override
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
            HttpServletRequest request) {

        statsClientService.sendHit("ewm-main-service", "/events", request.getRemoteAddr());

        PageRequest pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByState(EventState.PUBLISHED, pageable);

        if (text != null && !text.isBlank()) {
            String lower = text.toLowerCase();
            events = events.stream()
                    .filter(e -> (e.getAnnotation() != null && e.getAnnotation().toLowerCase().contains(lower)) ||
                            (e.getDescription() != null && e.getDescription().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
        }
        if (categories != null && !categories.isEmpty()) {
            events = events.stream()
                    .filter(e -> e.getCategory() != null && categories.contains(e.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (paid != null) {
            events = events.stream()
                    .filter(e -> e.getPaid() != null && e.getPaid().equals(paid))
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            events = events.stream()
                    .filter(e -> e.getEventDate() != null && !e.getEventDate().isBefore(rangeStart))
                    .collect(Collectors.toList());
        }
        if (rangeEnd != null) {
            events = events.stream()
                    .filter(e -> e.getEventDate() != null && !e.getEventDate().isAfter(rangeEnd))
                    .collect(Collectors.toList());
        }
        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> viewsMap = statsClientService.getViews(eventIds);

        List<EventShortDto> result = events.stream()
                .map(event -> EventMapper.toEventShortDtoAndViews(event, viewsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        if ("VIEWS".equalsIgnoreCase(sort)) {
            result.sort(Comparator.comparingLong(EventShortDto::getViews));
        } else if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            events.sort(Comparator.comparing(Event::getEventDate));
            result = events.stream()
                    .map(event -> EventMapper.toEventShortDtoAndViews(event, viewsMap.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        statsClientService.sendHit("ewm-main-service", "/events/" + eventId, request.getRemoteAddr());

        Map<Long, Long> viewsMap = statsClientService.getViews(List.of(eventId));
        Long views = viewsMap.getOrDefault(eventId, 0L);

        return EventMapper.toEventFullDtoAndViews(event, views);
    }


    private void checkIsEmpty(EventShortDto checkEvent, Event updateEvent) {

        String annotation = checkEvent.getAnnotation();
        if (annotation != null && !annotation.isEmpty()) {
            updateEvent.setAnnotation(annotation);
        }

        if ((checkEvent.getCategory() != null)) {


            updateEvent.setCategory(categoryRepository.findById(checkEvent.getCategory())
                    .orElseThrow(()
                            -> new NotFoundException("Не найдена заявка на участие в событии:" + checkEvent.getCategory())));
        }

        if (checkEvent.getConfirmedRequests() != null) {
            updateEvent.setConfirmedRequests(checkEvent.getConfirmedRequests());
        }

        String dateStr = checkEvent.getEventDate();
        if (dateStr != null && !dateStr.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(dateStr, formatter);

            if (timestamp.isBefore(LocalDateTime.now().plusHours(2))) {
                updateEvent.setEventDate(timestamp);
            } else {
                throw new ForbiddenException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента ");
            }
        }

        if (checkEvent.getInitiator() != null) {
            updateEvent.setInitiator(checkEvent.getInitiator());
        }

        if (checkEvent.getPaid() != null) {
            updateEvent.setPaid(checkEvent.getPaid());
        }

        String title = checkEvent.getTitle();
        if (title != null && !title.isEmpty()) {
            updateEvent.setTitle(title);
        }


    }


}
