package ru.practicum.main.event.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateEventUserRequest {

    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Location location;

    private boolean paid; //Новое значение флага о платности мероприятия

    private Integer participantLimit; // limit

    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    private String stateAction; //Изменение соcтояния события

    private String title;

}
