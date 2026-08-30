package ru.practicum.main.comments.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;

    private String text;

    private Long authorId;

    private Long eventId;

    private LocalDateTime created;

}
