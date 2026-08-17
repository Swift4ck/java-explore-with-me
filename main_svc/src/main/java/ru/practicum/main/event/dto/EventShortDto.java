package ru.practicum.main.event.dto;

import lombok.*;
import ru.practicum.main.category.Category;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventShortDto {

    private String annotation; //Краткое описаниe

    private Category category;

    private Integer confirmedRequests; //Количество одобренных заявок на участие в данном событии

    private String eventDate; //Дата и время на которые намечено событие (в формате \"yyyy-MM-dd HH:mm:ss\"

    private Long id;

    private Long initiator;

    private Boolean paid; //Нужно ли оплачивать участие

    private String title;

    private Long views;

}
