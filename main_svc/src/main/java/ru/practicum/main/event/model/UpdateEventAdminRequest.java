package ru.practicum.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateEventAdminRequest {

    private String annotation;

    private Long category;

    private String description;

    private String eventDate; //Новые дата и время на которые намечено событие. Дата и время указываются в формате \"yyyy-MM-dd HH:mm:ss\

    private Location location;

    private Boolean paid; //Новое значение флага о платности мероприятия

    private Integer participantLimit; //limit polsovatelei

    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    private String stateAction; //Новое состояние события

    private String title;

}
