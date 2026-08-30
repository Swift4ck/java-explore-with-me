package ru.practicum.main.subscriptions.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.subscriptions.model.Subscription;

@UtilityClass
public class SubscriptionMapper {

    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();

        subscriptionDto.setSubscriberId(subscription.getSubscriberId());
        subscriptionDto.setTargetId(subscription.getTargetId());
        subscriptionDto.setCreated(subscription.getCreated());

        return subscriptionDto;
    }
}
