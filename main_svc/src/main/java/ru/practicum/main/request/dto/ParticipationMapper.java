package ru.practicum.main.request.dto;

import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class ParticipationMapper {



    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String created = participationRequest.getCreated()
                .truncatedTo(ChronoUnit.MICROS)
                .format(formatter);

        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(created)
                .event(participationRequest.getEvent() != null ? participationRequest.getEvent().getId() : null)
                .requester(participationRequest.getRequester() != null ? participationRequest.getRequester().getId() : null)
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
