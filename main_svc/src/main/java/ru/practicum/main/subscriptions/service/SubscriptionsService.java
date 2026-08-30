package ru.practicum.main.subscriptions.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.subscriptions.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionsService {

    public SubscriptionDto createSubscription(Long userId, Long targetId);

    public ResponseEntity<Void> deleteSubscription(Long subscriberId, Long targetId);

    public List<SubscriptionDto> getAllSubscribe(Long subscriberId);

    public List<SubscriptionDto> getAllSubscribers(Long subscriptionId);

    public List<EventShortDto> eventsFeed(Long userId);


}
