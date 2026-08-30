package ru.practicum.main.comments.dto;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentDto {

    @Size(max = 255)
    private String text;

}
