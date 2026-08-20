package ru.practicum.main.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserMapper;
import ru.practicum.main.user.model.NewUserRequest;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Получен запрос на создания нового пользователя {}", newUserRequest);

        if (newUserRequest == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        String name = newUserRequest.getName();
        String email = newUserRequest.getEmail();

        if (name == null || name.isBlank()) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (name.length() < 2 || name.length() > 250) {
            throw new BadRequestException("Имя должно быть от 2 до 250 символов");
        }

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email не может быть пустым");
        }
        if (email.length() < 6 || email.length() > 254) {
            throw new BadRequestException("Email должен быть от 6 до 254 символов");
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            throw new BadRequestException("Некорректный формат email");
        }
        
        User saveUser = new User();
        saveUser.setEmail(email);
        saveUser.setName(name);

        try {
            User resultUser = userRepository.save(saveUser);
            return UserMapper.toUserDto(resultUser);
        } catch (DataIntegrityViolationException exp) {
            throw new ConflictException("пользователь с таким email уже существует");
        }

    }


    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Запрос на получения списка пользователей по id: {}", ids);

        if (ids == null || ids.isEmpty()) {
            PageRequest pageable = PageRequest.of(from, size);
            return userRepository.findAll(pageable).getContent()
                    .stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }

        List<UserDto> resultUser = new ArrayList<>();
        for (Long id : ids) {
            User findUser = userRepository.findById(id).orElse(null);
            if (findUser != null) {
                resultUser.add(UserMapper.toUserDto(findUser));
            }
        }
        return resultUser;
    }

    @Transactional
    @Override
    public ResponseEntity<Void> removeUser(Long userId) {

        log.info("Запроc на удаления пользователя с ID: {}", userId);

        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userRepository.delete(deleteUser);
        return ResponseEntity.noContent().build();
    }


}
