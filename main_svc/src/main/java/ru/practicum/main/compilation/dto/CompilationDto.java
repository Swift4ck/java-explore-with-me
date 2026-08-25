package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CompilationDto {

    private Long id;

    private Boolean pinned = false; //Закреплена ли подборка на главной странице сайта

    private String title; //Заголовок подборки

    private List<EventShortDto> events;

}
