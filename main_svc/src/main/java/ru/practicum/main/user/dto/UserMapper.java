package ru.practicum.main.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.user.model.User;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

}
