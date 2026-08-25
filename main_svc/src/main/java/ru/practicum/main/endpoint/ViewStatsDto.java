package ru.practicum.main.endpoint;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    private String app; //Название сервиса

    private String uri; //URI сервиса пример "example": "/events/1"

    private Long hits; // кол-во просмотров

}



