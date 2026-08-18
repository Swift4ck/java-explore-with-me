package ru.practicum.main.request.dto;

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
                .event(participationRequest.getEvent())
                .requester(participationRequest.getRequester())
                .status(participationRequest.getStatus())
                .build();
    }

    public static ParticipationRequest toParticipationRequest(
            ParticipationRequestDto participationRequestDto) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setId(participationRequestDto.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse(participationRequestDto.getCreated(), formatter);
        participationRequest.setCreated(timestamp);

        participationRequest.setEvent(participationRequestDto.getEvent());

        participationRequest.setRequester(participationRequestDto.getRequester());
        participationRequest.setStatus(participationRequestDto.getStatus());

        return participationRequest;
    }


    public static EventRequestStatusUpdateRequestDto toEventRequestStatusUpdateRequestDto(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequestDto) {

        return EventRequestStatusUpdateRequestDto.builder()
                .requestIds(eventRequestStatusUpdateRequestDto.getRequestIds())
                .status(eventRequestStatusUpdateRequestDto.getStatus())
                .build();
    }

}
