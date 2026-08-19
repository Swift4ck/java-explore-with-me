package ru.practicum.main.category.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewCategoryDto {

    @NotBlank(message = "Имя категории не может быть пустым")
    @Size(max = 50)
    private String name;

}
