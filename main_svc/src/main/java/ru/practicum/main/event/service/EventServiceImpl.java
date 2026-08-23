package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.endpoint.StatsClientService;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.Status;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventMapper;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.UpdateEventAdminRequest;
import ru.practicum.main.event.model.UpdateEventUserRequest;
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
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClientService statsClientService;
    private final CategoryRepository categoryRepository;

    private final Set<String> viewedIps = new HashSet<>();

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (userId == null || newEventDto == null) {
            throw new BadRequestException("Данные не корректные:" + userId + newEventDto);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timestamp = LocalDateTime.parse(newEventDto.getEventDate(), formatter);

        if (timestamp.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента: "
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

        Event getEvent = eventRepository.findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        return EventMapper.toEventFullDto(getEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен запрос на изменение мероприятия от пользователя:{} для мероприятия{}", userId, eventId);


        Event updateEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        if (updateEventUserRequest == null) {
            throw new BadRequestException("Данные не корректны");
        }

        if (updateEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (updateEventUserRequest.getAnnotation() != null && !updateEventUserRequest.getAnnotation().isEmpty()) {
            updateEvent.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            updateEvent.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID " + updateEventUserRequest.getCategory() + " не найдено)")));
        }

        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getDescription().isEmpty()) {
            updateEvent.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null && !updateEventUserRequest.getEventDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(updateEventUserRequest.getEventDate(), formatter);
            if (timestamp.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            }
            updateEvent.setEventDate(timestamp);
        }

        if (updateEventUserRequest.getLocation() != null) {
            updateEvent.setLocation(updateEventUserRequest.getLocation());
        }

        if (updateEventUserRequest.getPaid() != null) {
            updateEvent.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            updateEvent.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            updateEvent.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        String stateString = updateEventUserRequest.getStateAction();
        if (stateString != null && !stateString.trim().isEmpty()) {
            String action = stateString.trim().toUpperCase();
            if ("CANCEL_REVIEW".equals(action)) {
                if (updateEvent.getState() != EventState.PENDING) {
                    throw new ConflictException("Отменить можно только событие в статусе PENDING");
                }
                updateEvent.setState(EventState.CANCELED);
            } else if ("SEND_TO_REVIEW".equals(action)) {
                if (updateEvent.getState() != EventState.CANCELED) {
                    throw new ConflictException("Отправить на повторную модерацию можно только отменённое событие");
                }
                updateEvent.setState(EventState.PENDING);
            } else {
                throw new BadRequestException("Недопустимое значение состояния: " + stateString);
            }
        }

        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getTitle().isEmpty()) {
            updateEvent.setTitle(updateEventUserRequest.getTitle());
        }

        Event saveEvent = eventRepository.save(updateEvent);

        Long views = saveEvent.getViews() != null ? saveEvent.getViews() : 0L;
        return EventMapper.toEventFullDtoAndViews(saveEvent, views);
    }


    @Override
    public List<ParticipationRequestDto> getEventRequestsForUser(Long userId, Long eventId) {
        log.info("Запрос на получение информации о запросах текущего пользователя {}, на событии {}",
                userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID " + eventId + " не найдено"));

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId)
                .stream()
                .filter(pr -> pr.getEvent().getInitiator().equals(userId))
                .collect(Collectors.toList());

        return requests.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
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
                throw new ConflictException("Достигнут лимит одобренных заявок");
            }
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        int actualLimit = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
        int countCycle = 0;

        for (Long requestId : requestDto.getRequestIds()) {
            ParticipationRequest participationRequest = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Не найдена заявка на участие в событии:" + requestId));

            if (!participationRequest.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Заявка не относится к указанному событию");
            }

            if (!participationRequest.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Можно изменять только заявки в статусе PENDING");
            }

            if (newStatus == Status.CONFIRMED) {
                if (actualLimit + countCycle < event.getParticipantLimit()) {
                    participationRequest.setStatus(Status.CONFIRMED);
                    requestRepository.save(participationRequest);
                    confirmed.add(ParticipationMapper.toParticipationRequestDto(participationRequest));
                    countCycle++;
                } else {
                    participationRequest.setStatus(Status.REJECTED);
                    requestRepository.save(participationRequest);
                    rejected.add(ParticipationMapper.toParticipationRequestDto(participationRequest));
                }
            } else {
                participationRequest.setStatus(Status.REJECTED);
                requestRepository.save(participationRequest);
                rejected.add(ParticipationMapper.toParticipationRequestDto(participationRequest));
            }
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
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

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        List<Event> events = eventRepository.findAllByState(EventState.PUBLISHED);

        if (text != null && !text.isBlank() && !"0".equals(text)) {
            String lower = text.toLowerCase();
            events = events.stream()
                    .filter(e -> (e.getAnnotation() != null && e.getAnnotation().toLowerCase().contains(lower)) ||
                            (e.getDescription() != null && e.getDescription().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
        }

        if (categories != null && !categories.isEmpty()) {
            List<Long> validCategories = categories.stream()
                    .filter(c -> c != null && c != 0)
                    .collect(Collectors.toList());
            if (!validCategories.isEmpty()) {
                events = events.stream()
                        .filter(e -> e.getCategory() != null && validCategories.contains(e.getCategory().getId()))
                        .collect(Collectors.toList());
            }
        }

        if (paid != null) {
            events = events.stream()
                    .filter(e -> e.getPaid() != null && e.getPaid().equals(paid))
                    .collect(Collectors.toList());
        }

        LocalDateTime effectiveStart = rangeStart;
        LocalDateTime effectiveEnd = rangeEnd;
        if (effectiveStart != null && effectiveEnd != null && effectiveStart.isAfter(effectiveEnd)) {
            LocalDateTime temp = effectiveStart;
            effectiveStart = effectiveEnd;
            effectiveEnd = temp;
        }

        final LocalDateTime finalStart = effectiveStart;
        final LocalDateTime finalEnd = effectiveEnd;

        if (finalStart != null) {
            events = events.stream()
                    .filter(e -> e.getEventDate() != null && !e.getEventDate().isBefore(finalStart))
                    .collect(Collectors.toList());
        }
        if (finalEnd != null) {
            events = events.stream()
                    .filter(e -> e.getEventDate() != null && !e.getEventDate().isAfter(finalEnd))
                    .collect(Collectors.toList());
        }

        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if ("VIEWS".equalsIgnoreCase(sort)) {
            events.sort(Comparator.comparing(Event::getViews, Comparator.nullsLast(Comparator.naturalOrder())));
        } else {
            events.sort(Comparator.comparing(Event::getEventDate));
        }

        int start = Math.min(from, events.size());
        int end = Math.min(from + size, events.size());
        List<Event> page = events.subList(start, end);

        return page.stream()
                .map(event -> EventMapper.toEventShortDtoAndViews(event,
                        event.getViews() != null ? event.getViews() : 0L))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        statsClientService.sendHit("ewm-main-service", "/events/" + eventId, request.getRemoteAddr());

        String ip = request.getRemoteAddr();
        String key = eventId + ":" + ip;
        if (!viewedIps.contains(key)) {
            viewedIps.add(key);
            if (event.getViews() == null) {
                event.setViews(0L);
            }
            event.setViews(event.getViews() + 1);
            event = eventRepository.save(event);
        }

        return EventMapper.toEventFullDtoAndViews(event, event.getViews());
    }


    private void checkIsEmpty(EventShortDto checkEvent, Event updateEvent) {

        String annotation = checkEvent.getAnnotation();
        if (annotation != null && !annotation.isEmpty()) {
            updateEvent.setAnnotation(annotation);
        }

        if ((checkEvent.getCategory() != null)) {


            updateEvent.setCategory(categoryRepository.findById(checkEvent.getCategory().getId())
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
                throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            } else {
                updateEvent.setEventDate(timestamp);
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


    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        if (request == null) {
            throw new BadRequestException("Запрос не может быть пустым");
        }

        if (request.getAnnotation() != null) {
            if (request.getAnnotation().length() < 20 || request.getAnnotation().length() > 2000) {
                throw new BadRequestException("Аннотация должна быть от 20 до 2000 символов");
            }
            event.setAnnotation(request.getAnnotation());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().length() < 20 || request.getDescription().length() > 7000) {
                throw new BadRequestException("Описание должно быть от 20 до 7000 символов");
            }
            event.setDescription(request.getDescription());
        }

        if (request.getTitle() != null) {
            if (request.getTitle().length() < 3 || request.getTitle().length() > 120) {
                throw new BadRequestException("Заголовок должен быть от 3 до 120 символов");
            }
            event.setTitle(request.getTitle());
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID " + request.getCategory() + " не найдена"));
            event.setCategory(category);
        }

        if (request.getEventDate() != null && !request.getEventDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime eventDate;
            try {
                eventDate = LocalDateTime.parse(request.getEventDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Некорректный формат даты. Используйте yyyy-MM-dd HH:mm:ss");
            }

            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата начала события должна быть не ранее чем за 2 час от текущего момента");
            }
            event.setEventDate(eventDate);
        }

        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getStateAction() != null) {
            String action = request.getStateAction();
            if ("PUBLISH_EVENT".equals(action)) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Событие можно публиковать только из состояния ожидания публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equals(action)) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Событие уже опубликовано, отклонить его нельзя");
                }
                event.setState(EventState.CANCELED);
            } else {
                throw new ConflictException("Недопустимое значение stateAction: " + action);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(updatedEvent);
    }


    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        List<EventState> stateEnums = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                try {
                    stateEnums.add(EventState.valueOf(state.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Недопустимое значение состояния: " + state);
                }
            }
        }

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filtered = new ArrayList<>();
        for (Event event : allEvents) {
            boolean matches = true;

            if (users != null && !users.isEmpty()) {
                if (!users.contains(event.getInitiator())) {
                    matches = false;
                }
            }
            if (!stateEnums.isEmpty()) {
                if (!stateEnums.contains(event.getState())) {
                    matches = false;
                }
            }
            if (categories != null && !categories.isEmpty()) {
                if (event.getCategory() == null || !categories.contains(event.getCategory().getId())) {
                    matches = false;
                }
            }
            if (rangeStart != null) {
                if (event.getEventDate() == null || event.getEventDate().isBefore(rangeStart)) {
                    matches = false;
                }
            }
            if (rangeEnd != null) {
                if (event.getEventDate() == null || event.getEventDate().isAfter(rangeEnd)) {
                    matches = false;
                }
            }

            if (matches) {
                filtered.add(event);
            }
        }

        int total = filtered.size();
        int fromIndex = Math.min(from, total);
        int toIndex = Math.min(from + size, total);
        List<Event> pageEvents = filtered.subList(fromIndex, toIndex);

        List<Long> eventIds = new ArrayList<>();
        for (Event event : pageEvents) {
            eventIds.add(event.getId());
        }
        Map<Long, Long> viewsMap = statsClientService.getViews(eventIds);

        List<EventFullDto> result = new ArrayList<>();
        for (Event event : pageEvents) {
            EventFullDto dto = EventMapper.toEventFullDto(event);
            dto.setViews(viewsMap.getOrDefault(event.getId(), 0L));
            result.add(dto);
        }

        return result;
    }


}
