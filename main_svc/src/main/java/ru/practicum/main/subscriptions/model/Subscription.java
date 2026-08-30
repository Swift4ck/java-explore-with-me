package ru.practicum.main.subscriptions.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId; // Кто подписался

    @Column(name = "target_id", nullable = false)
    private Long targetId;     // На кого подписался

    @Column(name = "created")
    private LocalDateTime created;

}