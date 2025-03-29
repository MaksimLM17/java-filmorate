package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<UserDto> getUsers() {
        if (users.values().isEmpty()) {
            log.warn("Ошибка валидации, список пользователей пуст!");
            throw new NotFoundException("Список пользователей пуст");
        }
        log.info("Получен список пользователей.");
        return users.values().stream()
                .map(this::convertToDto)
                .toList();
    }

    @PostMapping
    public UserDto createUserDto(@Valid @RequestBody UserDto userDto) {
        User user = convertToEntity(userDto);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с именем {} добавлен", user.getName());
        return convertToDto(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        Integer id = userDto.getId();
        if (id == null) {
            log.error("Не указан id при запросе на обновление пользователя");
            throw new BadRequestException("Id должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.error("По переданному id {} пользователь не найден.", id);
            throw new NotFoundException(String.format("По переданному id: %d, пользователь не обнаружен",id));
        }
        User existingUser = users.get(id);
        User updatedUser = convertToEntity(userDto);
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null) {
            existingUser.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getBirthday() != null) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
        log.info("Пользователь с id {} обновлен", id);
        users.put(existingUser.getId(), existingUser);
        return convertToDto(existingUser);
    }

    private int getNextId() {
        int currentMaxId = users.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        return userDto;
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setLogin(userDto.getLogin());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        return user;
    }
}
