package ru.practicum.main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.Category;
import ru.practicum.main.event.model.Location;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewEventDto {


    private String annotation; //Краткое описание события

    private Long category; //id категории к которой относится событие

    private String description; //Полное описание события

    private String eventDate; //Дата и время на которые намечено событие. Дата и время указываются в формате \"yyyy-MM-dd HH:mm:ss\

    private Location location;

    private Boolean paid; //Нужно ли оплачивать участие в событии

    private Integer participantLimit = 0; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие. Если true, то все заявки будут ожидать подтверждения инициатором события. Если false - то будут подтверждаться автоматически.

    private String title; //Заголовок события

}
