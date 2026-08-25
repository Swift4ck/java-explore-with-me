package ru.practicum.main.request.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;
import ru.practicum.main.enums.Status;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created; //Дата и время создания заявки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; //Идентификатор события

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private User requester; //Идентификатор пользователя, отправившего заявку

    @Column(name = "status", nullable = false)
    private Status status; //Статус заявки

}

