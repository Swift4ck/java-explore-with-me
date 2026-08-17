package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    public ParticipationRequestDto createRequest(Long userId, Long eventId);

    public List<ParticipationRequestDto> getRequest(Long userId);

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId);

}
