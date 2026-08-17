package ru.practicum.main.request.model;

import lombok.*;
import ru.practicum.main.enums.Status;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds; //идентификаторы запросов на участие в событии текущего пользователя

    private Status status; //Новый статус запроса на участие в событии текущего пользователя

}
