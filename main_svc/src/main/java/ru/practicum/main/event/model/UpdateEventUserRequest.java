package ru.practicum.main.event.model;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private boolean paid; //Новое значение флага о платности мероприятия

    private Integer participantLimit; // limit

    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    private String stateAction; //Изменение соcтояния события

    @Size(min = 3, max = 120)
    private String title;

}
