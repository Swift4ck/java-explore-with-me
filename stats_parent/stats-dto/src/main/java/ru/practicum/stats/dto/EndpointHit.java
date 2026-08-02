package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EndpointHit {

    private Long id;

    private String app; //Идентификатор сервиса для которого записывается информация

    private String uri; //URI для которого был осуществлен запрос

    private String ip; //"IP-адрес пользователя, осуществившего запрос

    private String timestamp; //Дата и время, когда был совершен запрос к эндпоинту (в формате \"yyyy-MM-dd HH:mm:ss\")

}
