package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.Status;
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
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        log.info("Запрос на создания заявки на участие в мероприятие от пользователя:{}" +
                " от пользователя с id:{}", userId, eventId);


        ParticipationRequest check = requestRepository.findParticipationRequestByEventIdAndRequesterId(eventId, userId);

        if (check != null) {
            throw new ConflictException("Пользователь уже подал заявку");
        }

        Event checkEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID:" + eventId + "не найдено"));

        if (checkEvent.getInitiator().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии ");
        }

        if (checkEvent.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }


        if (checkEvent.getParticipantLimit() != 0) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (confirmedCount >= checkEvent.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников");
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());

        participationRequest.setEvent(checkEvent);

        participationRequest.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID:" + userId + "не найдено"))
        );

        if (checkEvent.getParticipantLimit() == 0 || !checkEvent.getRequestModeration()) {
            participationRequest.setStatus(Status.CONFIRMED);
        } else {
            participationRequest.setStatus(Status.PENDING);
        }

        ParticipationRequest saverRequest = requestRepository.save(participationRequest);


        return ParticipationMapper.toParticipationRequestDto(saverRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequest(Long userId) {

        log.info("Запрос на получение заявок текущего пользователя");

        if (userId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID:" + userId + "не найден"));

        List<ParticipationRequest> listRequest = requestRepository.findParticipationRequestByRequesterId(userId);

        return listRequest.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        log.info("Запрос на отмену своего запроса на участие в событие");

        ParticipationRequest cancelRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден или не доступен:" + requestId));

        if (!cancelRequest.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь может отменять только свои заявки");
        }

        if (cancelRequest.getStatus() == Status.CONFIRMED) {
            throw new ConflictException("Нельзя отменить уже принятую заявку");
        }

        cancelRequest.setStatus(Status.CANCELED);
        requestRepository.save(cancelRequest);
        return ParticipationMapper.toParticipationRequestDto(cancelRequest);
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
    @Transactional
    public ParticipationRequestDto confirmRequestByAdmin(Long eventId, Long reqId) {
        ParticipationRequest request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException("Заявка с id=" + reqId + " не найдена"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (!request.getEvent().getId().equals(eventId)) {
            throw new ConflictException("Заявка не относится к указанному событию");
        }

        if (request.getStatus() != Status.PENDING) {
            throw new ConflictException("Нельзя подтвердить заявку в статусе " + request.getStatus());
        }

        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит одобренных заявок");
            }
        }

        request.setStatus(Status.CONFIRMED);
        requestRepository.save(request);

        if (event.getParticipantLimit() > 0) {
            long confirmedNow = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (confirmedNow >= event.getParticipantLimit()) {
                List<ParticipationRequest> pendingRequests = requestRepository
                        .findAllByEventIdAndStatus(eventId, Status.PENDING);
                pendingRequests.forEach(r -> r.setStatus(Status.REJECTED));
                requestRepository.saveAll(pendingRequests);
            }
        }

        return ParticipationMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectRequestByAdmin(Long eventId, Long reqId) {
        ParticipationRequest request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException("Заявка с id=" + reqId + " не найдена"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (!request.getEvent().getId().equals(eventId)) {
            throw new ConflictException("Заявка не относится к указанному событию");
        }

        if (request.getStatus() != Status.PENDING) {
            throw new ConflictException("Нельзя отклонить заявку в статусе " + request.getStatus());
        }

        request.setStatus(Status.REJECTED);
        requestRepository.save(request);

        return ParticipationMapper.toParticipationRequestDto(request);
    }



}
