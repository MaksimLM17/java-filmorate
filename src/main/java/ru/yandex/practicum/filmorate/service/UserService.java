package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public Collection<UserDto> getAll() {
        log.debug("Получен запрос на получение списка пользователей!");
        if (userStorage.getAll().isEmpty()) {
            log.warn("Ошибка валидации, список пользователей пуст!");
            throw new NotFoundException("Список пользователей пуст");
        }
        return userStorage.getAll().stream()
                .map(userMapper::convertToDto)
                .toList();
    }

    public UserDto create(UserDto userDto) {
        log.debug("Получен запрос на создание пользователя с данными: {}", userDto);
        setDefaultNameIfEmpty(userDto);
        User user = userMapper.convertToEntity(userDto);
        return userMapper.convertToDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto) {
        log.debug("Получен запрос на обновление пользователя с данными: {}", userDto);
        validateUserId(userDto.getId());
        checkIdInDb(userDto.getId());

        User user = userMapper.convertToEntity(userDto);
        return userMapper.convertToDto(userStorage.update(user));
    }

    public void addFriend(Integer id, Integer friendId) {
        log.debug("Получен запрос на добавление в друзья с данными id: {} и friendId: {}", id, friendId);
        checkId(id, friendId);
        userStorage.addFriend(id,friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        log.debug("Получен запрос на удаление из друзей с данными id: {} и friendId: {}", id, friendId);
        checkId(id, friendId);
        userStorage.removeFriend(id, friendId);
    }

    public Collection<UserDto> getUsersFriends(Integer id) {
        log.debug("Получен запрос на получение списка друзей с данными id: {}", id);
        if (id <= 0) {
            log.error("В запросе получено некорректное значение id: {}", id);
            throw new BadRequestException("Id пользователя не может быть меньше или равно нулю!");
        }
        if (!userStorage.checkUserId(id)) {
            log.error("Не найден пользователь по id: {}", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден!");
        }
        return userStorage.getUsersFriends(id).stream()
                .map(userMapper::convertToDto)
                .toList();
    }

    public Collection<UserDto> getFriendsCommon(Integer id, Integer otherId) {
        log.debug("Получен запрос на получение общих друзей с данными id: {} и otherId: {}", id, otherId);
        checkId(id, otherId);
        return userStorage.getFriendsCommon(id, otherId).stream()
                .map(userMapper::convertToDto)
                .toList();
    }

    private void checkId(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            log.error("В запросе получено некорректное значение id: {} или friendId: {}", id, friendId);
            throw new BadRequestException("Id пользователя не может быть меньше или равно нулю!");
        }
        if (id.equals(friendId)) {
            log.error("Пользователь пытается добавить или удалить себя");
            throw new BadRequestException("Невозможно добавить или удалить самого себя");
        }
        if (!userStorage.checkUserId(id)) {
            log.error("Не найден пользователь по id: {}", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден!");
        }
        if (!userStorage.checkUserId(friendId)) {
            log.error("Не найден пользователь по friendId: {}", friendId);
            throw new NotFoundException("Пользователь с id: " + friendId + " не найден!");
        }
    }

    private void setDefaultNameIfEmpty(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            userDto.setName(userDto.getLogin());
        }
    }

    private void validateUserId(Integer id) {
        if (id == null) {
            log.error("Не указан id при запросе на обновление пользователя");
            throw new BadRequestException("Id должен быть указан");
        }
    }

    private void checkIdInDb(Integer id) {
        if (!userStorage.checkUserId(id)) {
            log.error("Не найден пользователь по id: {}", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден!");
        }
    }
}
