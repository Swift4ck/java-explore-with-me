package ru.practicum.main.request.dto;


import lombok.*;
import ru.practicum.main.enums.Status;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {

    private String created; //Дата и время создания заявки

    private Long event; //Идентификатор события

    private Long id;

    private Long requester; //Идентификатор пользователя, отправившего заявку

    private Status status; //Статус заявки

}
