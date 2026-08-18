package ru.practicum.main.request;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class RequestController {


    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @PathVariable Long eventId) {

//        authorizationVerification(userId);

        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getRequest(@PathVariable Long userId) {

//        authorizationVerification(userId);

        return requestService.getRequest(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @RequestParam Long requestId) {

//        authorizationVerification(userId);

        return requestService.cancelRequest(userId, requestId);
    }


//    public void authorizationVerification(Long userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
//
//        if (!(principal instanceof User)) {
//            throw new ForbiddenException("Пользователь не авторизован корректно");
//        }
//
//        User currentUser = (User) principal;
//        Long currentUserId = currentUser.getId();
//
//        if (!userId.equals(currentUserId)) {
//            throw new ForbiddenException("Нельзя создавать события от чужого имени авторизованы как пользователь:"
//                    + currentUserId + " но пытаетесь действовать от имени: " + userId
//            );
//        }
//    }

}
