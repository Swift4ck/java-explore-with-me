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
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
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

            if (checkEvent.getParticipantLimit().equals(checkEvent.getConfirmedRequests())) {
                throw new ConflictException("Достигнут лимит участников (лимит:" + checkEvent.getParticipantLimit() + ")");
            }

        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());

        participationRequest.setEvent(checkEvent);

        participationRequest.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID:" + userId + "не найдено"))
        );

        if (checkEvent.getRequestModeration()) {
            participationRequest.setStatus(Status.PENDING);
        } else {
            participationRequest.setStatus(Status.CONFIRMED);
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

        cancelRequest.setStatus(Status.CANCELED);

        return ParticipationMapper.toParticipationRequestDto(cancelRequest);
    }

}
