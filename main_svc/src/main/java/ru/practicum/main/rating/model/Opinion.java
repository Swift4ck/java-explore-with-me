package ru.practicum.main.rating.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.enums.Rating;

import java.time.LocalDateTime;

@Entity
@Table(name = "opinion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Кто проголосовал

    @Column(name = "event_id", nullable = false)
    private Long eventId; // За что проголосовали

    @Enumerated(EnumType.STRING)
    private Rating rating; //лайк или дислайк

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
