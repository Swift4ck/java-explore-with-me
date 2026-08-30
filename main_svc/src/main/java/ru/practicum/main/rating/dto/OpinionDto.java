package ru.practicum.main.rating.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.enums.Rating;

import java.time.LocalDateTime;

@Getter
@Setter
public class OpinionDto {

    private Long id;

    private Long userId; // Кто проголосовал

    private Long eventId; // За что проголосовали

    private Rating rating; //лайк или дислайк

    private LocalDateTime createdAt = LocalDateTime.now();

}
