package ru.practicum.main.request.dto;

import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParticipationMapper {


    public static ParticipationRequestDto toParticipationRequestDto(
            ParticipationRequest participationRequest) {

        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated().toString())
                .event(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }




    public static EventRequestStatusUpdateRequestDto toEventRequestStatusUpdateRequestDto(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequestDto) {

        return EventRequestStatusUpdateRequestDto.builder()
                .requestIds(eventRequestStatusUpdateRequestDto.getRequestIds())
                .status(eventRequestStatusUpdateRequestDto.getStatus())
                .build();
    }

}
