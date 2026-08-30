package ru.practicum.main.rating.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.enums.Rating;

@Getter
@Setter
public class CreateRatingDto {
    private Long userId;
    private Rating rating;
}
