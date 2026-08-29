package ru.practicum.main.subscriptions;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.subscriptions.dto.SubscriptionDto;
import ru.practicum.main.subscriptions.service.SubscriptionsService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class SubscriptionsController {

    private final SubscriptionsService subscriptionsService;


    @PostMapping("/{userId}/subscriptions/{targetId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto createSubscription(@PathVariable Long userId, @PathVariable Long targetId) {
        return subscriptionsService.createSubscription(userId, targetId);
    }

    @DeleteMapping("/{userId}/subscriptions/{targetId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long userId, @PathVariable Long targetId) {
        return subscriptionsService.deleteSubscription(userId, targetId);
    }

    @GetMapping("/{userId}/subscriptions")
    public List<SubscriptionDto> getAllSubscribe(@PathVariable Long userId) {
        return subscriptionsService.getAllSubscribe(userId);
    }

    @GetMapping("/{userId}/subscribers")
    public List<SubscriptionDto> getAllSubscribers(@PathVariable Long userId) {
        return subscriptionsService.getAllSubscribers(userId);
    }

    @GetMapping("/{userId}/feed")
    public List<EventShortDto> eventsFeed(@PathVariable Long userId) {
        return subscriptionsService.eventsFeed(userId);
    }


}
