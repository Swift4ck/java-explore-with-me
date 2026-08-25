package ru.practicum.main.compilation.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events; //Список id событий подборки для полной замены текущего списка

    private Boolean pinned; //Закреплена ли подборка на главной странице сайта

    @Size(max = 50)
    private String title; //Заголовок подборки

}
