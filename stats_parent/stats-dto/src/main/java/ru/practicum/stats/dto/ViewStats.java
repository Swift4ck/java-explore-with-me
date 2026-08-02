package ru.practicum.stats.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ViewStats {


    private String app; //Название сервиса

    private String uri; //URI сервиса пример "example": "/events/1"

    private Long hits; // кол-во просмотров


}
