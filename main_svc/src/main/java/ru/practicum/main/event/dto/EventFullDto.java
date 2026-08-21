package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id;

    private String annotation; //краткое описание

    private CategoryDto category;

    private LocalDateTime eventDate;

    private Long initiator; //кто создал

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocationDto location;

    private Boolean paid; //Нужно ли оплачивать участие

    private String title;//название

    private Integer confirmedRequests; //Количество одобренных заявок на участие в данном событии

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn; //Дата и время создания события (в формате \"yyyy-MM-dd HH:mm:ss\")

    private String description;

    private Integer participantLimit = 0; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; //Дата и время публикации события (в формате \"yyyy-MM-dd HH:mm:ss\")

    private Boolean requestModeration = true; //Нужна ли пре-модерация заявок на участие

    private EventState state; //Список состояний жизненного цикла события

    private Long views;
}