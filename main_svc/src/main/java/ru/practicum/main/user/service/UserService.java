package ru.practicum.main.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.NewUserRequest;

import java.util.List;

public interface UserService {

    public UserDto createUser(NewUserRequest newUserRequest);

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    public ResponseEntity<Void> removeUser(Long userId);

}
