package ru.practicum.main.user.dto;

import ru.practicum.main.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

}
