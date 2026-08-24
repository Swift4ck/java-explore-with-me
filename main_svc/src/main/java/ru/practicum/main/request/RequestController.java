package ru.practicum.main.request;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class RequestController {


    private final RequestService requestService;


    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam(required = true) Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getRequest(@PathVariable Long userId) {

        return requestService.getRequest(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {

        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateStatusRequest(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long eventId,
                                                  @PathVariable Long reqId) {
        return requestService.confirmRequestByAdmin(eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long eventId,
                                                 @PathVariable Long reqId) {
        return requestService.rejectRequestByAdmin(eventId, reqId);
    }




}
