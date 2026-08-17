package ru.practicum.main.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.main.enums.HttpStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private List<String> errors = new ArrayList<>(); //Список стектрейсов или описания ошибок

    private String message;

    private String reason; //Общее описание причины ошибки

    private HttpStatusEnum status; //Код статуса HTTP-ответа

    private LocalDateTime timestamp; //Дата и время когда произошла ошибка (в формате \"yyyy-MM-dd HH:mm:ss\"

}