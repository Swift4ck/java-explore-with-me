package ru.practicum.main.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    @Size(min = 6, max = 254)
    private String email; //Почтовый адрес

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250)
    private String name;


}
