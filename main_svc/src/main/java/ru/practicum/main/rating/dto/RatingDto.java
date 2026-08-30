package ru.practicum.main.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long eventId;

    private Long likesCount;

    private Long dislikesCount;

    private Long ratingScore = 0L;

    private LocalDateTime countingTime;
}
