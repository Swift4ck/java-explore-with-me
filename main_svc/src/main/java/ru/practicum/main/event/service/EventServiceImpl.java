package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

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

        newEvent.setInitiator(userId);
        newEvent.setState(EventState.PENDING);

        Event saveEvent = eventRepository.save(newEvent);

        return EventMapper.toEventFullDto(saveEvent);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.info("Получен запрос на получения мероприятий {} , {}", from, size);

        var pageable = PageRequest.of(from, size);

        var page = eventRepository.findByInitiator(userId, pageable);

        return page.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
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


    private void checkIsEmpty(EventShortDto checkEvent, Event updateEvent) {

        String annotation = checkEvent.getAnnotation();
        if (annotation != null && !annotation.isEmpty()) {
            updateEvent.setAnnotation(annotation);
        }

        if ((checkEvent.getCategory() != null)) {
            updateEvent.setCategory(checkEvent.getCategory());
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
