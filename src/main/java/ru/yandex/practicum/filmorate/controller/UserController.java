package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ValidationExceptionNotFound;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.Dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        if (users.values().isEmpty()) {
            log.warn("Ошибка валидации, список пользователей пуст!");
            throw new ValidationExceptionNotFound("Список пользователей пуст");
        }
        log.info("Получен список пользователей.");
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с именем {} добавлен", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody UserDto userDto) {
        Integer id = userDto.getId();
        if (id == null) {
            log.error("Не указан id при запросе на обновление пользователя");
            throw new ValidationExceptionNotFound("Id должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.error("По переданному id {} пользователь не найден.", id);
            throw new ValidationExceptionNotFound(String.format("По переданному id: %d, пользователь не обнаружен",id));
        }
        User newUser = users.get(id);
        if (userDto.getEmail() != null) {
            newUser.setEmail(userDto.getEmail());
        }
        if (userDto.getLogin() != null) {
            newUser.setLogin(userDto.getLogin());
        }
        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
        }
        if (userDto.getBirthday() != null) {
            newUser.setBirthday(userDto.getBirthday());
        }
        log.info("Пользователь с id {} обновлен", id);
        users.put(userDto.getId(), newUser);
        return newUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
