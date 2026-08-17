package ru.practicum.main.compilation.model;

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

    private boolean pinned; //Закреплена ли подборка на главной странице сайта

    private String title; //Заголовок подборки

}
