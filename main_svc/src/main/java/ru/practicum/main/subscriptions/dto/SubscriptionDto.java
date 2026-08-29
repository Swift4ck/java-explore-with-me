package ru.practicum.main.subscriptions.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionDto {

    private Long subscriberId; // Кто подписался

    private Long targetId;     // На кого подписался

    private LocalDateTime created;

}
