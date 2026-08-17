package ru.practicum.main.request.dto;

import lombok.*;
import ru.practicum.main.enums.Status;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventRequestStatusUpdateRequestDto {

    private List<Long> requestIds; //идентификаторы запросов на участие в событии текущего пользователя

    private Status status; //Новый статус запроса на участие в событии текущего пользователя

}
